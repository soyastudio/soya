package soya.framework.action.actions.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Command(group = "kafka", name = "seek-from", httpMethod = Command.HttpMethod.GET)
public class SeekFromAction extends KafkaConsumeAction<KafkaConsumeAction.ConsumerRecordMetadata[]> {

    @CommandOption(option = "p", required = true)
    private Integer partition;

    @CommandOption(option = "o", required = true)
    private Long offset;

    @Override
    protected ConsumerRecordMetadata[] execute() throws Exception {
        long timestamp = System.currentTimeMillis();
        List<ConsumerRecordMetadata> list = new ArrayList<>();

        KafkaConsumer<String, byte[]> consumer = kafkaClient().consumer();
        TopicPartition topicPartition = new TopicPartition(consumeTopic, partition);

        Collection<TopicPartition> partitions = Collections.singletonList(topicPartition);

        consumer.assign(partitions);
        consumer.seek(topicPartition, offset);

        while (!isTimeout(timestamp)) {
            ConsumerRecords<String, byte[]> records = consumer.poll(pollDuration());
            records.forEach(e -> {
                list.add(new ConsumerRecordMetadata(e));
            });

            consumer.commitSync();
        }

        Collections.sort(list);

        return list.toArray(new ConsumerRecordMetadata[list.size()]);
    }
}
