package soya.framework.action.actions.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "kafka", name = "fetch", httpMethod = Command.HttpMethod.GET)
public class FetchAction extends KafkaConsumeAction<String> {

    @CommandOption(option = "p", required = true)
    private Integer partition;

    @CommandOption(option = "o", required = true)
    private Long offset;

    @Override
    protected String execute() throws Exception {
        long timestamp = System.currentTimeMillis();
        List<ConsumerRecord<String, byte[]>> list = new ArrayList<>();

        KafkaConsumer<String, byte[]> consumer = kafkaClient().consumer();
        TopicPartition topicPartition = new TopicPartition(consumeTopic, partition);
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
            ConsumerRecord<String, byte[]> record = results.get(0);

            return new String(record.value());
        }
    }
}
