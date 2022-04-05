package soya.framework.kafka;

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
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class KafkaUtils {

    public static List<Metric> metrics(String env) {
        return new ArrayList<>(administrator(env).metrics().values());
    }

    public static String[] topics(String env, String filter) {
        List<String> results = new ArrayList<>();
        Future<Set<String>> future = administrator(env).listTopics().names();
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
                if (filter == null || e.startsWith(filter)) {
                    results.add(e);

                }
            });

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return results.toArray(new String[results.size()]);
    }

    public static ConsumerRecord<String, byte[]> consume(String topicName, String env) throws Exception {

        KafkaConsumer<String, byte[]> kafkaConsumer = consumer(env);
        List<String> topics = new ArrayList<>();
        topics.add(topicName);

        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<TopicPartition> partitions = partitionInfoSet.stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()))
                .collect(Collectors.toList());

        return latest(kafkaConsumer, topicName, partitions);
    }

    public static RecordMetadata produce(String topicName, Integer partition, String message, String key, Map<String, String> headers, long timeout, String env) throws Exception {
        return send(producer(env), createProducerRecord(topicName, partition, key, headers, message), timeout);
    }

    public static ConsumerRecord<String, byte[]> pubAndSub(String publishTopic, String message, String consumeTopic, long timeout, String env) throws Exception {

        long timestamp = System.currentTimeMillis();
        ProducerRecord<String, byte[]> record = createProducerRecord(publishTopic, 0, UUID.randomUUID().toString(), null, message);

        List<ConsumerRecord<String, byte[]>> results = new ArrayList<>();
        send(producer(env), record, (recordMetadata, e) -> {

            KafkaConsumer<String, byte[]> kafkaConsumer = consumer(env);
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(consumeTopic);
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
        });

        while (results.isEmpty()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new RuntimeException("Process timeout over " + timeout + "ms");
            }

            Thread.sleep(100l);
        }

        return results.get(0);
    }

    public static ProducerRecord<String, byte[]> createProducerRecord(String topicName, Integer partition, String key, Map<String, String> headers, String message) {

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

    public static KafkaProducer producer(String environment) {
        return KafkaClientFactory.getInstance(environment).createKafkaProducer();
    }

    public static KafkaConsumer consumer(String environment) {
        return KafkaClientFactory.getInstance(environment).createKafkaConsumer();
    }

    public static AdminClient administrator(String environment) {
        return KafkaClientFactory.getInstance(environment).createAdminClient();
    }

}
