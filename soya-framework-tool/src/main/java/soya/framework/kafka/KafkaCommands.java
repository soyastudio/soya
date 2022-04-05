package soya.framework.kafka;

import com.google.gson.*;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class KafkaCommands {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    protected static CommandLineParser parser = new DefaultParser();
    protected static Options options = new Options();
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected static Map<String, Method> commands = new LinkedHashMap<>();

    static {
        Method[] methods = KafkaCommands.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Command.class) != null) {
                commands.put(method.getName(), method);
            }
        }

        // Command line definition:
        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());

        options.addOption(Option.builder("c")
                .longOpt("consumerTopic")
                .hasArg(true)
                .desc("Consumer Topic")
                .required(false)
                .build());

        options.addOption(Option.builder("d")
                .longOpt("producerTopic")
                .hasArg(true)
                .desc("Producer Topic.")
                .required(false)
                .build());

        options.addOption(Option.builder("e")
                .longOpt("env")
                .hasArg(true)
                .desc("Environment, default 'local', case insensitive.")
                .required(false)
                .build());

        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("File related to the action specified.")
                .required(false)
                .build());

        options.addOption(Option.builder("h")
                .longOpt("header")
                .hasArg(true)
                .desc("Kafka message header")
                .required(false)
                .build());

        options.addOption(Option.builder("k")
                .longOpt("key")
                .hasArg(true)
                .desc("Kafka message key")
                .required(false)
                .build());

        options.addOption(Option.builder("m")
                .longOpt("msg")
                .hasArg(true)
                .desc("Message for Kafka producer")
                .required(false)
                .build());

        options.addOption(Option.builder("n")
                .longOpt("count")
                .hasArg(true)
                .desc("Number of message to print out.")
                .required(false)
                .build());

        options.addOption(Option.builder("o")
                .longOpt("Offset")
                .hasArg(true)
                .desc("Offset.")
                .required(false)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("partition")
                .hasArg(true)
                .desc("Partition.")
                .required(false)
                .build());

        options.addOption(Option.builder("q")
                .longOpt("query")
                .hasArg(true)
                .desc("Query.")
                .required(false)
                .build());

        options.addOption(Option.builder("r")
                .longOpt("decoder")
                .hasArg(true)
                .desc("Avro decoder, default is 'json' other value is 'binary'")
                .required(false)
                .build());

        options.addOption(Option.builder("s")
                .longOpt("schema")
                .hasArg(true)
                .desc("Avro schema file name")
                .required(false)
                .build());

        options.addOption(Option.builder("t")
                .longOpt("timestamp")
                .hasArg(true)
                .desc("Timestamp for polling records after or before")
                .required(false)
                .build());
    }

    public static void configure(Properties properties) {
        KafkaClientFactory.configure(properties);
    }

    public static CommandLine build(String cl) throws ParseException {
        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(cl);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            list.add(token);
        }

        return build(list.toArray(new String[list.size()]));
    }

    public static CommandLine build(String[] args) throws ParseException {
        return parser.parse(options, args);
    }

    public static CommandLine build(JsonObject jsonObject) throws ParseException {
        List<String> list = new ArrayList<>();
        jsonObject.entrySet().forEach(e -> {
            String key = e.getKey();
            if (options.hasOption(e.getKey())) {
                list.add("-" + key);
                if (options.getOption(key).hasArg()) {
                    list.add(e.getValue().getAsString());
                }
            }
        });

        return build(list.toArray(new String[list.size()]));
    }

    public static String execute(CommandLine cmd) throws Exception {
        String action = "";
        if (cmd.hasOption("a")) {
            action = cmd.getOptionValue("a");
        }

        Method method = KafkaCommands.class.getMethod(action, new Class[]{CommandLine.class});
        if (method.getAnnotation(Command.class) == null) {
            throw new IllegalArgumentException("Not command method: " + method.getName());
        }
        Command command = method.getAnnotation(Command.class);
        Opt[] opts = command.options();
        for (Opt opt : opts) {
            if (opt.required() && cmd.getOptionValue(opt.option()) == null) {
                throw new IllegalArgumentException("Option '" + opt.option() + "' is required.");
            }
        }

        return (String) method.invoke(null, new Object[]{cmd});

    }

    @Command(
            desc = "Help",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "help",
                            desc = "Command name."),
                    @Opt(option = "q",
                            desc = "Query for help topic")
            },
            cases = {"-a help"}
    )
    public static String help(CommandLine cmd) {
        String query = cmd.getOptionValue("q");

        if (query == null) {
            JsonArray array = new JsonArray();
            commands.entrySet().forEach(e -> {
                Method method = e.getValue();
                array.add(commandDesc(method));
            });

            return GSON.toJson(array);

        } else if (query.length() == 1 && options.hasOption(query)) {
            Option option = options.getOption(query);
            JsonObject jo = new JsonObject();
            jo.addProperty("option", option.getOpt());
            jo.addProperty("longOption", option.getLongOpt());
            jo.addProperty("hasArg", option.hasArg());
            jo.addProperty("description", option.getDescription());
            return GSON.toJson(jo);

        } else if (query.length() > 1 && commands.containsKey(query)) {
            return GSON.toJson(commandDesc(commands.get(query)));

        } else {
            return "Can not find help topic.";
        }
    }

    @Command(
            desc = "Kafka metrics",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "metrics",
                            desc = "Command name."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment")
            },
            cases = {"-a metrics", "-a metrics -e QA"}
    )
    public static String metrics(CommandLine cmd) {
        List<Metric> metricList = new ArrayList<>(kafkaClientFactory(cmd).createAdminClient().metrics().values());
        return GSON.toJson(metricList);
    }

    @Command(
            desc = "Display topics of specified environment base on query prefix, show all topics on local environment by default.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "topics",
                            desc = "Command name."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "q",
                            desc = "Filter with prefix")
            },
            cases = {"-a topics", "-a topics -q ESED_C01_ -e QA"}
    )
    public static String topics(CommandLine cmd) {
        List<String> results = new ArrayList<>();

        String q = null;
        try {
            q = cmd.getOptionValue("q");

        } catch (Exception e) {

        }

        String query = q;

        AdminClient adminClient = kafkaClientFactory(cmd).createAdminClient();
        Future<Set<String>> future = adminClient.listTopics().names();
        while (!future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            List<String> topics = new ArrayList<>(future.get());
            Collections.sort(topics);
            topics.forEach(e -> {
                if (query == null || e.startsWith(query)) {
                    results.add(e);

                }
            });

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return GSON.toJson(results);
    }

    @Command(
            desc = "Display topic metadata of specified environment.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "topic",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment")
            },
            cases = {"-a topic -c MY_TOPIC_NAME", "-a topic -c MY_TOPIC_NAME -e QA"}
    )
    public static String topic(CommandLine cmd) {
        if (!cmd.hasOption("c")) {
            throw new IllegalArgumentException("Please specify topic name using command line argument '-c'");
        }

        String topicName = cmd.getOptionValue("c");
        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();

        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<PartitionStatus> partitionStatuses = partitionInfoSet.stream()
                .map(partitionInfo -> new PartitionStatus(partitionInfo))
                .collect(Collectors.toList());

        Collection<TopicPartition> partitions = partitionStatuses.stream()
                .map(e -> e.topicPartition)
                .collect(Collectors.toList());
        kafkaConsumer.assign(partitions);

        Map<TopicPartition, Long> beginOffsets = kafkaConsumer.beginningOffsets(partitions);
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(partitions);

        partitionStatuses.forEach(e -> {
            if (beginOffsets.containsKey(e.topicPartition)) {
                e.offset.begin = beginOffsets.get(e.topicPartition);
            }

            if (endOffsets.containsKey(e.topicPartition)) {
                e.offset.end = endOffsets.get(e.topicPartition);
            }

        });

        return GSON.toJson(partitionStatuses);
    }

    @Command(
            desc = "Produce message to specified topic and Consume message from another topic on specified environment.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "pubAndSub",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name."),
                    @Opt(option = "d",
                            required = true,
                            desc = "Topic name for produce messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "h",
                            desc = "Headers for produce record, gzip and base64 encoded json format."),
                    @Opt(option = "k",
                            desc = "Key for produce record, if not specified, generate a uuid."),
                    @Opt(option = "m",
                            required = true,
                            desc = "Message to publish, in GZip and Base64 encoded format."),
                    @Opt(option = "r",
                            required = false,
                            defaultValue = "json",
                            desc = "Avro message decoder, values: 'json', 'binary"),
                    @Opt(option = "s",
                            desc = "Schema to parse the avro message if provided"),
                    @Opt(option = "t",
                            defaultValue = "5000",
                            desc = "Timeout for connection.")
            },
            cases = {"-a pubAndSub -d MY_TOPIC_NAME -m BASE64_GZIP_MSG",
                    "-a pubAndSub -d MY_TOPIC_NAME -m BASE64_GZIP_MSG -k 1234567 -h BASE64_GZIP_JSON_HEADERS -e QA"}
    )
    public static String pubAndSub(CommandLine cmd) throws Exception {

        long timestamp = System.currentTimeMillis();

        String topicName = topicName = cmd.getOptionValue("d");
        byte[] msg = extract(cmd.getOptionValue("m"));
        long timeout = 5000L;

        if (cmd.hasOption("d")) {
            topicName = cmd.getOptionValue("d");
        }

        String obTopic = cmd.getOptionValue("c");

        String key = cmd.hasOption("k") ? cmd.getOptionValue("k") : UUID.randomUUID().toString();

        String hs = cmd.hasOption("h") ? cmd.getOptionValue("h") : null;
        Headers headers = headers(hs);

        ProducerRecord<String, byte[]> record = createProducerRecord(topicName, msg, headers, key);

        List<ConsumerRecord<String, byte[]>> results = new ArrayList<>();
        send(kafkaClientFactory(cmd).createKafkaProducer(), record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
                List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(obTopic);
                Collection<TopicPartition> partitions = partitionInfoSet.stream()
                        .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                                partitionInfo.partition()))
                        .collect(Collectors.toList());


                Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);
                for (TopicPartition partition : partitions) {
                    if (results.isEmpty()) {
                        List<TopicPartition> assignments = new ArrayList<>();
                        assignments.add(partition);
                        kafkaConsumer.assign(assignments);

                        Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
                        kafkaConsumer.seek(partition, Math.max(0, latestOffset));
                        ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));

                        polled.forEach(rc -> {
                            if (rc.timestamp() > recordMetadata.timestamp()) {
                                results.add(0, rc);
                            }
                        });

                    }
                }
            }
        });

        while (results.isEmpty()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new RuntimeException("Process timeout over " + timeout + "ms");
            }

            Thread.sleep(100l);
        }

        ConsumerRecord<String, byte[]> rc = results.get(0);
        if (cmd.hasOption("s")) {
            Schema schema = null;
            String sc = cmd.getOptionValue("s");
            if (new File(sc).exists()) {
                schema = new Schema.Parser().parse(new File(sc));
            }

            if (schema == null) {
                try {
                    URL url = new URL(sc);
                    schema = new Schema.Parser().parse(url.openStream());

                } catch (Exception e) {

                }
            }

            if (schema == null) {
                schema = new Schema.Parser().parse(decompress(sc));
            }

            GenericRecord avro = read(rc.value(), schema, cmd.getOptionValue("r"));

            return avro.toString();

        } else {
            return render(rc);

        }
    }

    @Command(
            desc = "Produce message to specified topic of specified environment.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "produce",
                            desc = "Command name."),
                    @Opt(option = "d",
                            required = true,
                            desc = "Topic name for produce messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "h",
                            desc = "Headers for produce record, gzip and base64 encoded json format."),
                    @Opt(option = "k",
                            desc = "Key for produce record, if not specified, generate a uuid."),
                    @Opt(option = "m",
                            required = true,
                            desc = "Message to publish, in GZip and Base64 encoded format."),
                    @Opt(option = "t",
                            defaultValue = "5000",
                            desc = "Timeout for connection.")
            },
            cases = {"-a produce -d MY_TOPIC_NAME -m BASE64_GZIP_MSG",
                    "-a produce -d MY_TOPIC_NAME -m BASE64_GZIP_MSG -k 1234567 -h BASE64_GZIP_JSON_HEADERS -e QA"}
    )
    public static String produce(CommandLine cmd) throws Exception {
        String topicName = topicName = cmd.getOptionValue("d");
        byte[] msg = extract(cmd.getOptionValue("m"));
        long timeout = 5000L;

        if (cmd.hasOption("d")) {
            topicName = cmd.getOptionValue("d");
        }

        String key = cmd.hasOption("k") ? cmd.getOptionValue("k") : UUID.randomUUID().toString();

        String hs = cmd.hasOption("h") ? cmd.getOptionValue("h") : null;
        Headers headers = headers(hs);

        ProducerRecord<String, byte[]> record = createProducerRecord(topicName, msg, headers, key);
        RecordMetadata metadata = send(kafkaClientFactory(cmd).createKafkaProducer(), record, timeout);
        if (metadata != null) {
            return prettyPrintJson(record, metadata);
        }

        JsonObject result = new JsonObject();

        return GSON.toJson(result);

    }

    @Command(
            desc = "Consume latest message from specified topic of specified environment.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "consume",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for consuming messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "r",
                            required = false,
                            defaultValue = "json",
                            desc = "Avro message decoder, values: 'json', 'binary"),
                    @Opt(option = "s",
                            desc = "Schema to parse the message if provided")
            },
            cases = {"-a consume -c MY_TOPIC_NAME",
                    "-a consume -c MY_TOPIC_NAME -e QA",
                    "-a consume -c MY_TOPIC_NAME -e QA -s AVSC_URL_OR_COMPRESSED"}
    )
    public static String consume(CommandLine cmd) throws Exception {

        String topicName = null;
        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(topicName);

        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("p")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("p"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        ConsumerRecord<String, byte[]> rc = latest(kafkaConsumer, topicName, partitions);
        if (rc != null) {
            String msg = new String(rc.value()).trim();
            if (cmd.hasOption("s")) {
                Schema schema = null;
                String avsc = cmd.getOptionValue("s");

                try {
                    String decompressed = decompress(avsc);
                    schema = new Schema.Parser().parse(decompressed);

                } catch (Exception e) {
                    // do nothing
                }

                if (schema == null) {
                    try {
                        URL url = new URL(avsc);
                        InputStream inputStream = url.openStream();
                        schema = new Schema.Parser().parse(inputStream);
                    } catch (Exception e) {
                        // do nothing
                    }
                }

                if (schema == null) {
                    try {
                        File file = new File(avsc);
                        schema = new Schema.Parser().parse(file);
                    } catch (Exception e) {
                        // do nothing
                    }
                }

                if (schema == null) {
                    throw new IllegalArgumentException("Cannot parse avro schema from 's', it should be a compressed string or url or file");
                }

                byte[] data = rc.value();

                GenericRecord record = read(data, schema, cmd.getOptionValue("r"));
                String json = record.toString();

                JsonObject result = new JsonObject();
                result.addProperty("timestamp", DATE_FORMAT.format(new Date(rc.timestamp())));
                result.addProperty("topic", rc.topic());
                result.addProperty("partition", rc.partition());
                result.addProperty("offset", rc.offset());
                result.addProperty("key", rc.key());
                result.add("headers", GSON.toJsonTree(rc.headers().toArray()));
                result.add("value", JsonParser.parseString(json));

                return GSON.toJson(result);
            }

            if (msg.startsWith("<") && msg.endsWith(">")) {
                return prettyPrintXml(rc);

            } else {
                return prettyPrintJson(rc);
            }

        }

        return null;
    }

    @Command(
            desc = "Consume latest avro message from specified topic of specified environment and parse the avro record payload with specified avro schema.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "consume",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for consuming messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "f",
                            required = true,
                            desc = "Path to export file."),
                    @Opt(option = "r",
                            required = false,
                            defaultValue = "json",
                            desc = "Avro message decoder, values: 'json', 'binary"),
                    @Opt(option = "s",
                            required = true,
                            desc = "Path to avro schema file.")
            },
            cases = {"-a read -c MY_TOPIC_NAME -f XXX.xxx"}
    )
    public static String read(CommandLine cmd) throws Exception {
        String topicName = null;
        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        Schema schema = null;
        String avsc = cmd.getOptionValue("s");

        try {
            String decompressed = decompress(avsc);
            schema = new Schema.Parser().parse(decompressed);

        } catch (Exception e) {
            // do nothing
        }

        if (schema == null) {
            try {
                URL url = new URL(avsc);
                InputStream inputStream = url.openStream();
                schema = new Schema.Parser().parse(inputStream);
            } catch (Exception e) {
                // do nothing
            }
        }

        if (schema == null) {
            try {
                File file = new File(avsc);
                schema = new Schema.Parser().parse(file);
            } catch (Exception e) {
                // do nothing
            }
        }

        if (schema == null) {
            throw new IllegalArgumentException("Cannot parse avro schema from 's', it should be a compressed string or url or file");
        }

        File file = new File(cmd.getOptionValue("f"));
        if (file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(topicName);

        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("p")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("p"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        ConsumerRecord<String, byte[]> rc = latest(kafkaConsumer, topicName, partitions);
        if (rc != null) {
            byte[] data = rc.value();
            GenericRecord record = read(data, schema, cmd.getOptionValue("r"));
            write((GenericData.Record) record, schema, file);

        }

        return "";
    }

    @Command(
            desc = "Consume latest avro message from specified topic of specified environment and parse the avro record payload with specified avro schema.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "consume",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for consuming messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "r",
                            required = false,
                            defaultValue = "json",
                            desc = "Avro message decoder, values: 'json', 'binary"),
                    @Opt(option = "s",
                            required = true,
                            desc = "Path to avro schema file.")
            },
            cases = {"-a avro -c MY_TOPIC_NAME -s xxx.avsc",
                    "-a consume -c MY_TOPIC_NAME -s xxx.avsc -e QA"}
    )
    public static String avro(CommandLine cmd) throws Exception {
        String topicName = null;
        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        Schema schema = null;
        String avsc = cmd.getOptionValue("s");

        try {
            String decompressed = decompress(avsc);
            schema = new Schema.Parser().parse(decompressed);

        } catch (Exception e) {
            // do nothing
        }

        if (schema == null) {
            try {
                URL url = new URL(avsc);
                InputStream inputStream = url.openStream();
                schema = new Schema.Parser().parse(inputStream);
            } catch (Exception e) {
                // do nothing
            }
        }

        if (schema == null) {
            try {
                File file = new File(avsc);
                schema = new Schema.Parser().parse(file);
            } catch (Exception e) {
                // do nothing
            }
        }

        if (schema == null) {
            throw new IllegalArgumentException("Cannot parse avro schema from 's', it should be a compressed string or url or file");
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(topicName);

        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("P")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("P"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        ConsumerRecord<String, byte[]> rc = latest(kafkaConsumer, topicName, partitions);

        if (rc != null) {
            byte[] data = rc.value();

            GenericRecord record = read(data, schema, cmd.getOptionValue('r'));
            return record.toString();

        }

        return null;
    }

    @Command(
            desc = "Get message from specified topic, partition and offset of specified environment.",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "get",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for consuming messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "o",
                            required = true,
                            desc = "Offset"),
                    @Opt(option = "p",
                            required = true,
                            desc = "Partition"),
                    @Opt(option = "s",
                            desc = "Schema to parse the message if provided"),
                    @Opt(option = "t",
                            defaultValue = "1000",
                            desc = "Timeout.")

            },
            cases = {"-a get -c MY_TOPIC_NAME -p 0 -o 123 -e QA -t 5000"}
    )
    public static String get(CommandLine cmd) throws Exception {

        KafkaConsumer kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();

        String topicName = cmd.getOptionValue("c");

        long timeout = 1000;
        if (cmd.hasOption("t")) {
            timeout = Long.parseLong(cmd.getOptionValue("t"));
        }

        ConsumerRecord<String, byte[]> record = null;
        if (cmd.hasOption("p") && cmd.hasOption("o")) {
            record = consume(kafkaConsumer, topicName, Integer.parseInt(cmd.getOptionValue("p")), Long.parseLong(cmd.getOptionValue("o")), timeout);

        }

        if (record != null) {
            /*log("Timestamp: " + DATE_FORMAT.format(new Date(record.timestamp())));
            log("Topic: " + record.topic());
            log("Partition: " + record.partition());
            log("Offset: " + record.offset());
            log("Key: " + record.key());
            log("Headers: " + toString(record.headers()));
            log("Message: ");
            log(prettyPrint());*/

            return new String(record.value());

        }

        return null;
    }

    @Command(
            desc = "Poll specified topic of specified environment from begin to end and for record information",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "pollToEnd",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for polling messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "p",
                            desc = "Partition to poll, if not specified, poll every partition one by one."),
                    @Opt(option = "t",
                            defaultValue = "30000",
                            desc = "Polling timeout for each partition.")
            },
            cases = {"-a pollToEnd -c MY_TOPIC_NAME",
                    "-a pollToEnd -c MY_TOPIC_NAME -p 0 -t 60000 -e QA"}
    )
    public static String pollToEnd(CommandLine cmd) throws Exception {
        Set<RecordInfo> results = new HashSet<>();
        String topicName = null;

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("p")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("p"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        long timeout = 30000L;
        if (cmd.hasOption("t")) {
            timeout = Long.parseLong(cmd.getOptionValue("t"));
        }

        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);
            kafkaConsumer.assign(assignments);
            kafkaConsumer.seekToBeginning(assignments);
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));

            polled.forEach(rc -> {
                results.add(new RecordInfo(rc));
            });
        }

        List<RecordInfo> list = new ArrayList<>(results);
        Collections.sort(list);

        return GSON.toJson(list);

    }

    @Command(
            desc = "Poll specified topic of specified environment from begin to end and for record information",
            options = {
                    @Opt(option = "a",
                            required = true,
                            defaultValue = "pollToEnd",
                            desc = "Command name."),
                    @Opt(option = "c",
                            required = true,
                            desc = "Topic name for polling messages."),
                    @Opt(option = "e",
                            defaultValue = "LOCAL",
                            desc = "Environment"),
                    @Opt(option = "p",
                            desc = "Partition to poll, if not specified, poll every partition one by one."),
                    @Opt(option = "t",
                            desc = "Polling timeout for each partition.")
            },
            cases = {"-a pollToEnd -c MY_TOPIC_NAME",
                    "-a pollToEnd -c MY_TOPIC_NAME -p 0 -t 60000 -e QA"}
    )
    public static String pollToBegin(CommandLine cmd) throws Exception {
        Set<RecordInfo> results = new HashSet<>();
        String topicName = null;

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("p")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("p"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        long timeout = 30000L;
        if (cmd.hasOption("t")) {
            timeout = Long.parseLong(cmd.getOptionValue("t"));
        }

        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);
            kafkaConsumer.assign(assignments);
            kafkaConsumer.seekToEnd(assignments);
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));

            polled.forEach(rc -> {
                results.add(new RecordInfo(rc));
            });
        }

        List<RecordInfo> list = new ArrayList<>(results);
        Collections.sort(list);
        Collections.reverse(list);

        return GSON.toJson(list);

    }

    public static List<RecordInfo> since(CommandLine cmd) throws Exception {
        Set<RecordInfo> results = new HashSet<>();
        String topicName = null;

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        if (topicName == null) {
            throw new IllegalArgumentException("Topic is not set. Please set topic name using parameter 'c'.");
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClientFactory(cmd).createKafkaConsumer();
        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("p")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("p"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        long timeout = 60000L;
        if (cmd.hasOption("t")) {
            timeout = Long.parseLong(cmd.getOptionValue("t"));
        }

        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);
            kafkaConsumer.assign(assignments);
            kafkaConsumer.seekToBeginning(assignments);
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));

            polled.forEach(rc -> {
                results.add(new RecordInfo(rc));
            });
        }

        List<RecordInfo> list = new ArrayList<>(results);
        Collections.sort(list);

        return list;

    }

    // Utilities:
    protected static JsonObject commandDesc(Method method) {
        Command annotation = method.getAnnotation(Command.class);

        JsonObject obj = new JsonObject();
        obj.addProperty("command", method.getName());
        obj.addProperty("description", annotation.desc());

        JsonArray opts = new JsonArray();
        Opt[] options = annotation.options();
        for (Opt opt : options) {
            JsonObject o = new JsonObject();
            o.addProperty("option", opt.option());
            o.addProperty("required", opt.required());
            o.addProperty("defaultValue", opt.defaultValue());
            o.addProperty("description", opt.desc());
            opts.add(o);
        }
        obj.add("options", opts);

        obj.add("example", GSON.toJsonTree(annotation.cases()));

        return obj;
    }

    protected static RecordMetadata send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, long timeout) throws Exception {

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

    protected static void send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, Callback callback) throws Exception {

        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record, callback);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > 60000) {
                throw new TimeoutException("Fail to publish message in 60 second.");
            }

            Thread.sleep(100L);
        }

    }

    protected static ProducerRecord<String, byte[]> createProducerRecord(String topicName, byte[] msg, Headers headers, String key) {
        RecordBuilder builder = new RecordBuilder(topicName).key(key).value(msg);
        if (headers != null) {
            headers.forEach(e -> {
                builder.header(e.key(), new String(e.value()));
            });
        }

        return builder.create();
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

        } else {
            String token = decompress(headers);
            JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
            jsonObject.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getAsString().getBytes()));
            });
        }

        return recordHeaders;
    }

    protected static ConsumerRecord<String, byte[]> latest(KafkaConsumer kafkaConsumer, String topicName, Collection<TopicPartition> partitions) throws Exception {
        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);

            kafkaConsumer.assign(assignments);
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset));
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(5000));

            polled.forEach(rc -> {
                records.add(rc);
            });
        }

        ConsumerRecord<String, byte[]> rc = null;
        for (ConsumerRecord<String, byte[]> record : records) {
            if (rc == null) {
                rc = record;
            } else if (record.timestamp() > rc.timestamp()) {
                rc = record;
            }
        }

        return rc;
    }

    protected static ConsumerRecord<String, byte[]> consume(KafkaConsumer kafkaConsumer, String topicName, int partition, long offset, long timeout) {
        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        TopicPartition topicPartition = new TopicPartition(topicName, partition);

        Collection<TopicPartition> partitions = new ArrayList<>();
        partitions.add(topicPartition);
        kafkaConsumer.assign(partitions);
        kafkaConsumer.seekToBeginning(partitions);

        ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));
        polled.forEach(rc -> {
            if (rc.offset() == offset) {
                records.add(rc);
            }
        });

        return records.isEmpty() ? null : records.get(0);
    }

    protected static GenericRecord read(byte[] data, Schema schema, String decoderType) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = null;

        if(decoderType == null || decoderType.equalsIgnoreCase("json")) {
            decoder = DecoderFactory.get().jsonDecoder(schema, new ByteArrayInputStream(data));

        } else if(decoderType.equalsIgnoreCase("binary")) {
            decoder = DecoderFactory.get().binaryDecoder(data, null);

        } else {
            throw new IllegalArgumentException("Cannot find avro decoder: " + decoderType);

        }



        return datumReader.read(null, decoder);
    }

    protected static void write(GenericData.Record record, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);

        dataFileWriter.append(record);
        dataFileWriter.close();

    }

    protected static KafkaClientFactory kafkaClientFactory(CommandLine command) {
        String env = "LOCAL";
        if (command.hasOption("e")) {
            env = command.getOptionValue("e");
        }

        return KafkaClientFactory.getInstance(env);
    }

    protected static String prettyPrintJson(ProducerRecord<String, byte[]> record, RecordMetadata metadata) {
        JsonObject result = new JsonObject();
        result.addProperty("timestamp", DATE_FORMAT.format(new Date(metadata.timestamp())));
        result.addProperty("topic", metadata.topic());
        result.addProperty("partition", metadata.partition());
        result.addProperty("offset", metadata.offset());
        result.addProperty("key", record.key());

        Header[] headers = record.headers().toArray();
        for (Header header : headers) {

        }
        result.add("value", JsonParser.parseString(new String(record.value())));

        return GSON.toJson(result);
    }

    protected static String render(ConsumerRecord<String, byte[]> rc) throws TransformerException {
        String msg = new String(rc.value()).trim();
        if (msg.startsWith("<") && msg.endsWith(">")) {
            return prettyPrintXml(msg);

        } else if (msg.startsWith("{") && msg.endsWith("}") || msg.startsWith("[") && msg.endsWith("]")) {
            return GSON.toJson(JsonParser.parseString(msg));

        } else {
            return new String(rc.value());

        }
    }

    protected static String prettyPrintJson(ConsumerRecord<String, byte[]> rc) {

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

    protected static byte[] extract(String str) {
        byte[] result = null;

        File file = new File(str);
        if (file.exists()) {
            try {
                result = IOUtils.toByteArray(new FileInputStream(file));

            } catch (IOException e) {

            }
        }

        if (result == null) {
            try {
                URL url = new URL(str);
                result = IOUtils.toByteArray(url);

            } catch (Exception e) {

            }
        }

        if (result == null) {
            try {
                result = decompress(str).getBytes(StandardCharsets.UTF_8);

            } catch (Exception e) {
                // do nothing
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Cannot extract data from file, url or string: " + str);
        }

        return result;
    }

    protected static String compress(String contents) {
        return Base64.getEncoder().encodeToString(zip(contents));
    }

    protected static String decompress(String contents) {
        return unzip(Base64.getDecoder().decode(contents));
    }

    protected static byte[] zip(final String str) {
        if ((str == null) || (str.length() == 0)) {
            throw new IllegalArgumentException("Cannot zip null or empty string");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip content", e);
        }
    }

    protected static String unzip(final byte[] compressed) {
        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }
        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }

    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
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

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Command {
        String desc() default "";

        Opt[] options() default {};

        String[] cases() default {};
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Opt {
        String option();

        boolean required() default false;

        String defaultValue() default "";

        String desc() default "";
    }
}
