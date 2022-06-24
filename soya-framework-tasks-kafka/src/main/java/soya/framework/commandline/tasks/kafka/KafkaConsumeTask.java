package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.CommandOption;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public abstract class KafkaConsumeTask<T> extends KafkaTask<T> {
    public static final long DEFAULT_POLL_DURATION = 5000l;

    @CommandOption(option = "c", required = true)
    protected String consumeTopic;

    @CommandOption(option = "d")
    protected Long pollDuration;

    protected List<ConsumerRecord<String, byte[]>> poll(Collection<TopicPartition> partitions, KafkaConsumer<String, byte[]> consumer) {
        long timestamp = System.currentTimeMillis();
        List<ConsumerRecord<String, byte[]>> list = new ArrayList<>();

        consumer.assign(partitions);
        consumer.seekToBeginning(partitions);
        while (!isTimeout(timestamp)) {
            ConsumerRecords<String, byte[]> records = consumer.poll(pollDuration());

            consumer.commitSync();
        }

        return list;
    }

    protected Duration pollDuration() {
        return Duration.ofMillis(pollDuration == null? DEFAULT_POLL_DURATION : pollDuration);
    }

    protected ConsumerRecord<String, byte[]> latest(Collection<TopicPartition> partitions) throws IOException {

        KafkaConsumer<String, byte[]> kafkaConsumer = consumer();

        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        Map<TopicPartition, Long> latestOffsets = consumer().endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);

            kafkaConsumer.assign(assignments);
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset));
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(pollDuration());
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

    protected ConsumerRecord<String, byte[]> fetch(TopicPartition topicPartition, Long offset) throws IOException {
        long timestamp = System.currentTimeMillis();

        KafkaConsumer<String, byte[]> consumer = kafkaClient().consumer();
        consumer.assign(Collections.singletonList(topicPartition));
        consumer.seek(topicPartition, offset);

        List<ConsumerRecord<String, byte[]>> results = new ArrayList<>();
        while (!isTimeout(timestamp) && results.size() == 0) {
            ConsumerRecords<String, byte[]> records = consumer.poll(pollDuration());
            records.forEach(e -> {
                if (e.offset() == offset) {
                    results.add(e);
                }
            });

            consumer.commitSync();

            if (consumer.position(topicPartition) > offset) {
                break;
            }

        }

        if (results.size() == 0) {
            return null;

        } else {
            return results.get(0);
        }
    }

    static class ConsumerRecordMetadata implements Comparable<ConsumerRecordMetadata> {
        private final String topic;
        private final int partition;
        private final long offset;
        private final Date timestamp;
        private final int serializedKeySize;
        private final int serializedValueSize;
        private final Map<String, String> headers;
        private final String key;

        ConsumerRecordMetadata(ConsumerRecord consumeRecord) {
            this.topic = consumeRecord.topic();
            this.partition = consumeRecord.partition();
            this.offset = consumeRecord.offset();
            this.timestamp = new Date(consumeRecord.timestamp());
            this.serializedKeySize = consumeRecord.serializedKeySize();
            this.serializedValueSize = consumeRecord.serializedValueSize();
            this.key = toString(consumeRecord.key());
            this.headers = new LinkedHashMap<>();
            consumeRecord.headers().forEach(e -> {
                headers.put(e.key(), new String(e.value()));
            });
        }

        private String toString(Object key) {
            if (key == null) {
                return null;
            } else {
                return key.toString();
            }
        }

        @Override
        public int compareTo(ConsumerRecordMetadata o) {
            return o.timestamp.compareTo(timestamp);
        }
    }

}
