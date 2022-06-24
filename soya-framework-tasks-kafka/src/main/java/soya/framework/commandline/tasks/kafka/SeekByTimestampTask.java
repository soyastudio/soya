package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Command(group = "kafka", name = "seek-by-timestamp", httpMethod = Command.HttpMethod.GET)
public class SeekByTimestampTask extends KafkaConsumeTask<ConsumerRecord[]> {

    @CommandOption(option = "s", required = true)
    private String startTime;

    @CommandOption(option = "e")
    private String endTime;

    @Override
    protected ConsumerRecord[] execute() throws Exception {

        long endTimestamp = endTime == null ? System.currentTimeMillis() : DATE_FORMAT.parse(endTime).getTime();
        long startTimestamp = startTime == null ? (endTimestamp - 3600000) : DATE_FORMAT.parse(startTime).getTime();

        KafkaConsumer<String, byte[]> kafkaConsumer = consumer();
        Collection<TopicPartition> partitions = partitions(consumeTopic);

        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        Map<TopicPartition, Long> latestOffsets = consumer().endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);
            kafkaConsumer.assign(assignments);
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1000);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset));
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(5000));
            polled.forEach(rc -> {
                records.add(rc);
            });
        }

        List<ConsumerRecord<String, byte[]>> results = new ArrayList<>();
        for (ConsumerRecord<String, byte[]> record : records) {
            if (record.timestamp() > startTimestamp && record.timestamp() < endTimestamp) {
                results.add(record);
            }
        }

        return results.toArray(new ConsumerRecord[results.size()]);
    }
}
