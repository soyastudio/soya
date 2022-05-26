package soya.framework.tasks.apache.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import soya.framework.commandline.Command;

import java.util.UUID;

@Command(group = "kafka", name = "produce")
public class ProduceTask extends AbstractProduceTask {

    @Override
    public String execute() throws Exception {
        try {

            System.out.println("============== " + message);

            RecordMetadata metadata = KafkaUtils.produce(produceTopic, 0, message, UUID.randomUUID().toString(), null, timeout, environment);
            return metadata.toString();

        } catch (Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }
}
