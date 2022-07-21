package soya.framework.action.actions.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Command(group = "kafka", name = "topic", httpMethod = Command.HttpMethod.GET)
public class TopicInfoAction extends KafkaAction<PartitionStatus[]> {

    @CommandOption(option = "t", required = true)
    private String topicName;

    @Override
    protected PartitionStatus[] execute() throws Exception {
        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaClient().consumer();

        Map<TopicPartition, PartitionInfo> partitionPartitionInfoMap = new LinkedHashMap<>();
        kafkaConsumer.partitionsFor(topicName).forEach(e -> {
            partitionPartitionInfoMap.put(new TopicPartition(e.topic(), e.partition()), e);
        });

        List<TopicPartition> partitions = new ArrayList<>(partitionPartitionInfoMap.keySet());
        kafkaConsumer.assign(partitions);
        Map<TopicPartition, Long> beginOffsets = kafkaConsumer.beginningOffsets(partitions);
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(partitions);

        List<PartitionStatus> list = new ArrayList<>();
        partitionPartitionInfoMap.entrySet().forEach(e -> {
            list.add(new PartitionStatus(e.getValue(), beginOffsets.get(e.getKey()), endOffsets.get(e.getKey())));
        });

        return list.toArray(new PartitionStatus[list.size()]);
    }
}
