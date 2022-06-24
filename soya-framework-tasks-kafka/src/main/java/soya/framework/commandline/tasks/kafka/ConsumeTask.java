package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.util.Collections;

@Command(group = "kafka", name = "consume", httpMethod = Command.HttpMethod.GET)
public class ConsumeTask extends KafkaConsumeTask<String> {

    @CommandOption(option = "p")
    private Integer partition;

    @CommandOption(option = "o")
    private Long offset;

    @Override
    protected String execute() throws Exception {
        ConsumerRecord<String, byte[]> record = null;
        if(partition == null || partition.intValue() < 0) {
            record = latest(partitions(consumeTopic));

        } else if(offset == null || offset.longValue() < 0) {
            record = latest(Collections.singletonList(new TopicPartition(consumeTopic, partition)));

        } else {
            record = fetch(new TopicPartition(consumeTopic, partition), offset);
        }

        if(record == null) {
            return null;

        } else {
            return new String(record.value());
        }
    }
}
