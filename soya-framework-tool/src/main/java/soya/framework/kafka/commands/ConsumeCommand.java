package soya.framework.kafka.commands;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "consume", httpMethod = Command.HttpMethod.GET)
public class ConsumeCommand extends AbstractConsumeCommand {

    @CommandOption(option = "f", longOption = "format")
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

