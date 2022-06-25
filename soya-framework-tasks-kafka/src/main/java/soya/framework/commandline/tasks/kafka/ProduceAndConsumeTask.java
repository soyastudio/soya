package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Command(group = "kafka", name = "produce-and-consume")
public class ProduceAndConsumeTask extends ProduceTask {
    public static final long DEFAULT_POLL_DURATION = 5000l;

    @CommandOption(option = "c", required = true)
    protected String consumeTopic;

    @CommandOption(option = "d")
    protected Long pollDuration;

    @Override
    protected Object execute() throws Exception {
        long timestamp = System.currentTimeMillis();

        KafkaProducer<String, byte[]> producer = producer();
        KafkaConsumer<String, byte[]> kafkaConsumer = consumer();
        ProducerRecord<String, byte[]> record = createProducerRecord(produceTopic, partition, UUID.randomUUID().toString(), message, null);

        List<ConsumerRecord<String, byte[]>> results = new ArrayList<>();

        send(producer, record, (recordMetadata, e) -> {
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
                    ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(pollDuration());

                    polled.forEach(rc -> {
                        if (rc.timestamp() > recordMetadata.timestamp()) {
                            results.add(0, rc);
                        }
                    });
                }
            }
        });

        while (results.isEmpty()) {
            if (isTimeout(timestamp)) {
                throw new RuntimeException("Process timeout over " + timeout() + " ms");
            }

            Thread.sleep(100l);
        }

        if(results.isEmpty()) {
            return null;
        } else {
            return new String(results.get(0).value());
        }
    }

    private void send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, Callback callback) throws Exception {

        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record, callback);
        while (!future.isDone()) {
            if (isTimeout(timestamp)) {
                throw new TimeoutException("Fail to publish message in 60 second.");
            }

            Thread.sleep(100L);
        }

        kafkaProducer.close();

    }

    private Duration pollDuration() {
        return Duration.ofMillis(pollDuration == null? DEFAULT_POLL_DURATION : pollDuration);
    }
}
