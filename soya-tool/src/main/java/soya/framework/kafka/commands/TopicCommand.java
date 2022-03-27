package soya.framework.kafka.commands;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(group = "kafka", name = "topic")
public class TopicCommand extends KafkaCommand {

    @CommandOption(option = "c", longOption = "topic", required = true)
    private String topicName;

    @Override
    public String call() throws Exception {
        KafkaConsumer<String, byte[]> kafkaConsumer = createKafkaConsumer();

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
}
