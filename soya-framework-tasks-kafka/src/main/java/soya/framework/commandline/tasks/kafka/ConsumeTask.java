package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

@Command(group = "kafka", name = "consume", httpMethod = Command.HttpMethod.GET)
public class ConsumeTask extends AbstractConsumeTask {

    @CommandOption(option = "f")
    protected String format;

    @Override
    public String execute() throws Exception {
        try {
            ConsumerRecord<String, byte[]> rc = KafkaUtils.consume(consumeTopic, environment);

            return new String(rc.value());

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
}

