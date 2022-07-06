package soya.framework.action.actions.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Command(group = "kafka", name = "latest", httpMethod = Command.HttpMethod.GET)
public class LatestAction extends KafkaConsumeAction<String> {

    @CommandOption(option = "f")
    protected String format;

    @Override
    protected String execute() throws Exception {
        KafkaConsumer<String, byte[]> kafkaConsumer = consumer();
        Collection<TopicPartition> partitions = partitions(consumeTopic);

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

        if(rc != null) {
            return new String(rc.value());

        } else {
            return null;

        }
    }

}
