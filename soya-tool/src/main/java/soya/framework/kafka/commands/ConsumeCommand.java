package soya.framework.kafka.commands;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "consume")
public class ConsumeCommand extends KafkaCommand {

    @CommandOption(option = "c", longOption = "consumeTopic", required = true)
    protected String consumeTopic;

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

