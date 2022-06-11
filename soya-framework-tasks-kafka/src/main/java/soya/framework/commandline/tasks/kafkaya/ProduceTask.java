package soya.framework.commandline.tasks.kafkaya;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Command(group = "kafka", name = "produce")
public class ProduceTask extends KafkaTask {

    @CommandOption(option = "i", required = true)
    protected String produceTopic;

    @CommandOption(option = "p")
    protected Integer partition;

    @CommandOption(option = "k")
    protected String keySerializer;

    @CommandOption(option = "v")
    protected String valueSerializer;

    @CommandOption(option = "m", required = true, dataForProcessing = true)
    protected String message;

    @Override
    protected Object execute() throws Exception {
        KafkaProducer producer = producer();

        return null;
    }
/*

    public RecordMetadata produce(String topicName, Integer partition, String message, String key, Map<String, String> headers, long timeout, KafkaProducer producer) throws Exception {
        long timestamp = System.currentTimeMillis();
        Future<RecordMetadata> future = producer().send(record);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new TimeoutException("Fail to publish message to: " + record.key() + " in " + timeout + "ms.");
            }

            Thread.sleep(100L);
        }

        producer.close();

        return future.get();
    }
*/

    protected RecordMetadata send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, long timeout) throws Exception {

        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new TimeoutException("Fail to publish message to: " + record.key() + " in " + timeout + "ms.");
            }

            Thread.sleep(100L);
        }

        kafkaProducer.close();

        return future.get();
    }

    public ProducerRecord<String, byte[]> createProducerRecord(String topicName, Integer partition, String key, Map<String, String> headers, String message) {

        RecordHeaders recordHeaders = new RecordHeaders();
        if (headers != null) {
            headers.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getBytes()));
            });
        }

        return new ProducerRecord<String, byte[]>(topicName,
                partition == null ? 0 : partition,
                key,
                message.getBytes(StandardCharsets.UTF_8),
                recordHeaders);
    }

}
