package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Command(group = "kafka", name = "produce")
public class ProduceTask extends KafkaTask {

    @CommandOption(option = "i", required = true)
    protected String produceTopic;

    @CommandOption(option = "p")
    protected Integer partition;

    @CommandOption(option = "k")
    protected String keySerializer;

    @CommandOption(option = "v")
    protected String valueSerializer;

    @CommandOption(option = "m", required = true, dataForProcessing = true)
    protected String message;

    @Override
    protected Object execute() throws Exception {
        return send(producer(), createProducerRecord(produceTopic, partition, null, message, null), timeout());
    }

    private RecordMetadata send(KafkaProducer<String, byte[]> kafkaProducer, ProducerRecord<String, byte[]> record, long timeout) throws Exception {
        long timestamp = System.currentTimeMillis();

        Future<RecordMetadata> future = kafkaProducer.send(record);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new TimeoutException("Fail to publish message to: " + record.key() + " in " + timeout + "ms.");
            }

            Thread.sleep(100L);
        }

        kafkaProducer.close();

        return future.get();
    }

}
