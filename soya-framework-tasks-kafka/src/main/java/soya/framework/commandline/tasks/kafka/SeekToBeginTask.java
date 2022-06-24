package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Command(group = "kafka", name = "seek-to-begin", httpMethod = Command.HttpMethod.GET)
public class SeekToBeginTask extends KafkaConsumeTask<KafkaConsumeTask.ConsumerRecordMetadata[]> {

    @CommandOption(option = "p")
    private Integer partition;

    @Override
    protected ConsumerRecordMetadata[] execute() throws Exception {
        long timestamp = System.currentTimeMillis();
        List<ConsumerRecordMetadata> list = new ArrayList<>();

        KafkaConsumer<String, byte[]> consumer = kafkaClient().consumer();
        Collection<TopicPartition> partitions = partition == null? partitions(consumeTopic) : Collections.singletonList(new TopicPartition(consumeTopic, partition));

        consumer.assign(partitions);
        consumer.seekToBeginning(partitions);

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
