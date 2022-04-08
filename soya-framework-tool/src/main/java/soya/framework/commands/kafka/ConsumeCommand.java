package soya.framework.commands.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "kafka", name = "consume", httpMethod = Command.HttpMethod.GET)
public class ConsumeCommand extends AbstractConsumeCommand {

    @CommandOption(option = "f")
    protected String format;

    @Override
    public String call() throws Exception {
        try {
            ConsumerRecord<String, byte[]> rc = KafkaUtils.consume(consumeTopic, environment);

            return new String(rc.value());

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
}

