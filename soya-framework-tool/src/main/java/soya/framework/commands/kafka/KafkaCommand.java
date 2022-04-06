package soya.framework.commands.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;
import soya.framework.commands.kafka.KafkaClientFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public abstract class KafkaCommand implements CommandCallable<String> {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @CommandOption(option = "e", longOption = "env", paramType = CommandOption.ParamType.PathParam)
    protected String environment = "LOCAL";

    @CommandOption(option = "t", longOption = "timeout")
    protected Long timeout = Long.valueOf(30000l);

    protected KafkaProducer createKafkaProducer() {
        return KafkaClientFactory.getInstance(environment).createKafkaProducer();
    }

    protected KafkaConsumer createKafkaConsumer() {
        return KafkaClientFactory.getInstance(environment).createKafkaConsumer();
    }

    protected AdminClient createAdminClient() {
        return KafkaClientFactory.getInstance(environment).createAdminClient();
    }

    protected static Headers headers(String headers) {
        if (headers == null) {
            return null;
        }

        RecordHeaders recordHeaders = new RecordHeaders();
        if (headers.startsWith("[") && headers.endsWith("]")) {
            String[] arr = headers.substring(1, headers.length() - 1).split(";");
            for (String exp : arr) {
                if (exp.contains("=")) {
                    String[] kv = exp.split("=");
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    if (value.startsWith("'") && value.endsWith("'") || value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    recordHeaders.add(key, value.getBytes());
                }
            }

        } /*else {
            String token = decompress(headers);
            JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
            jsonObject.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getAsString().getBytes()));
            });
        }*/

        return recordHeaders;
    }

    protected ProducerRecord<String, byte[]> createProducerRecord(String topicName, byte[] msg, Headers headers, String key) {
        RecordBuilder builder = new RecordBuilder(topicName).key(key).value(msg);
        if (headers != null) {
            headers.forEach(e -> {
                builder.header(e.key(), new String(e.value()));
            });
        }

        return builder.create();
    }

    protected RecordMetadata send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, long timeout) throws Exception {

        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new TimeoutException("Fail to publish message to: " + record.key() + " in " + timeout + "ms.");
            }

            Thread.sleep(100L);
        }

        return future.get();
    }

    protected void send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, Callback callback) throws Exception {

        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record, callback);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > 60000) {
                throw new TimeoutException("Fail to publish message in 60 second.");
            }

            Thread.sleep(100L);
        }

    }

    protected String prettyPrintJson(ConsumerRecord<String, byte[]> rc) {

        JsonObject result = new JsonObject();
        result.addProperty("timestamp", DATE_FORMAT.format(new Date(rc.timestamp())));
        result.addProperty("topic", rc.topic());
        result.addProperty("partition", rc.partition());
        result.addProperty("offset", rc.offset());
        result.addProperty("key", rc.key());
        result.add("headers", GSON.toJsonTree(rc.headers().toArray()));
        try {
            result.add("value", JsonParser.parseString(new String(rc.value())));

        } catch (Exception e) {
            result.addProperty("value", new String(rc.value()));
        }

        return GSON.toJson(result);
    }

    protected static String prettyPrintXml(ConsumerRecord<String, byte[]> rc) throws TransformerException {
        StringBuilder builder = new StringBuilder();
        builder.append("<consumer-record>");
        builder.append("<timestamp>").append(DATE_FORMAT.format(new Date(rc.timestamp()))).append("</timestamp>");
        builder.append("<topic>").append(rc.topic()).append("</topic>");
        builder.append("<partition>").append(rc.partition()).append("</partition>");
        builder.append("<offset>").append(rc.offset()).append("</offset>");
        if (rc.key() != null) {
            builder.append("<key>").append(rc.key()).append("</key>");
        }

        Header[] headers = rc.headers().toArray();
        for (Header header : headers) {
            builder.append("<header>")
                    .append("<key>").append(header.key()).append("</key>")
                    .append("<value>").append(header.value()).append("</value>")
                    .append("</header>");
        }

        builder.append("<value>").append(new String(rc.value())).append("</value>");
        builder.append("</consumer-record>");

        return prettyPrintXml(builder.toString());
    }

    protected static String prettyPrintXml(String xml) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());
        Source xmlInsetProperty = new StreamSource(new StringReader(xml));
        transformer.transform(xmlInsetProperty, result);
        return result.getWriter().toString();
    }

    // Inner classes:
    static class PartitionStatus extends PartitionInfo {
        protected transient TopicPartition topicPartition;
        protected OffsetInfo offset = new OffsetInfo();

        public PartitionStatus(PartitionInfo partitionInfo) {
            super(partitionInfo.topic(), partitionInfo.partition(),
                    partitionInfo.leader(), partitionInfo.replicas(), partitionInfo.inSyncReplicas(), partitionInfo.offlineReplicas());
            this.topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
        }
    }

    static class OffsetInfo {
        protected long begin;
        protected long end;
    }

    static class RecordInfo implements Comparable<RecordInfo> {
        protected ConsumerRecord<String, byte[]> record;

        protected transient long ts;

        protected String timestamp;
        protected int partition;
        protected long offset;
        protected String key;
        protected int size;
        protected String headers;

        protected RecordInfo(ConsumerRecord<String, byte[]> record) {
            this.ts = record.timestamp();
            this.partition = record.partition();
            this.offset = record.offset();
            this.timestamp = DATE_FORMAT.format(new Date(record.timestamp()));
            this.key = record.key();
            this.size = record.serializedValueSize();
            //this.headers = record.headers() == null ? "" : toString(record.headers());
        }

        @Override
        public int compareTo(RecordInfo o) {
            return (int) (ts - o.ts);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RecordInfo)) return false;

            RecordInfo that = (RecordInfo) o;

            if (partition != that.partition) return false;
            return offset == that.offset;
        }

        @Override
        public int hashCode() {
            int result = partition;
            result = 31 * result + (int) (offset ^ (offset >>> 32));
            return result;
        }
    }

    static class RecordBuilder {
        protected String topic;
        protected Integer partition = 0;
        protected Long timestamp;
        protected String key;
        protected byte[] value;
        protected Map<String, String> headers = new HashMap<>();

        protected RecordMetadata metadata;

        public RecordBuilder(String topic) {
            this.topic = topic;
        }

        public RecordBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RecordBuilder partition(int partition) {
            this.partition = partition;
            return this;
        }

        public RecordBuilder generateKey() {
            this.key = UUID.randomUUID().toString();
            return this;
        }

        public RecordBuilder key(String key) {
            this.key = key;
            return this;
        }

        public RecordBuilder value(byte[] value) {
            this.value = value;
            return this;
        }

        public RecordBuilder message(String msg) {
            this.value = msg.getBytes();
            return this;
        }

        public RecordBuilder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public RecordBuilder metadata(RecordMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public ProducerRecord<String, byte[]> create() {
            RecordHeaders recordHeaders = new RecordHeaders();
            headers.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getBytes()));
            });

            return new ProducerRecord<String, byte[]>(topic, partition, key, value, recordHeaders);
        }
    }

}
