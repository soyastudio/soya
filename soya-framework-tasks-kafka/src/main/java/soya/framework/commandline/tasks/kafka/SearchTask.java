package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Command(group = "kafka", name = "search", httpMethod = Command.HttpMethod.GET)
public class SearchTask extends KafkaConsumeTask<KafkaConsumeTask.ConsumerRecordMetadata[]> {
    public static final int DEFAULT_SEARCH_NUMBER = 1000;

    @CommandOption(option = "w", required = true)
    private String keyword;

    @CommandOption(option = "n")
    private Integer number;

    @Override
    protected ConsumerRecordMetadata[] execute() throws Exception {
        int searchNum = number != null ? number : DEFAULT_SEARCH_NUMBER;
        byte[] pattern = keyword.getBytes(StandardCharsets.UTF_8);

        KafkaConsumer<String, byte[]> kafkaConsumer = consumer();
        Collection<TopicPartition> partitions = partitions(consumeTopic);

        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();

        Map<TopicPartition, Long> latestOffsets = consumer().endOffsets(partitions);
        long timestamp = System.currentTimeMillis();
        while (!isTimeout(timestamp)) {
            for (TopicPartition partition : partitions) {
                List<TopicPartition> assignments = new ArrayList<>();
                assignments.add(partition);

                kafkaConsumer.assign(assignments);
                Long latestOffset = Math.max(0, latestOffsets.get(partition) - searchNum);
                kafkaConsumer.seek(partition, Math.max(0, latestOffset));
                ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(pollDuration());

                polled.forEach(rc -> {

                    System.out.println(rc.partition() + "-" + rc.offset());
                    byte[] v = rc.value();
                    String msg = new String(v);

                    if(msg.contains(keyword)) {
                        //System.out.println(rc.partition() + "-" + rc.offset());
                    }

                    /*if (indexOf(v, pattern) >= 0) {
                        System.out.println(rc.partition() + "-" + rc.offset());
                        records.add(rc);
                    }*/

                });
            }

            kafkaConsumer.commitSync();

        }

        return new ConsumerRecordMetadata[0];
    }

    public static int indexOf(byte[] data, byte[] pattern) {
        int[] failure = computeFailure(pattern);

        int j = 0;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

}
