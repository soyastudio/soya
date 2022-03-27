package soya.framework.kafka.commands;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;
import soya.framework.commons.cli.Resources;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "pub-and-sub")
public class PubAndSubCommand extends AbstractProduceCommand {

    @CommandOption(option = "c", longOption = "consumeTopic", required = true)
    protected String consumeTopic;

    @Override
    public String call() throws Exception {
        String msg = Resources.getResourceAsString(message);
        ConsumerRecord<String, byte[]> record = KafkaUtils.pubAndSub(produceTopic, msg, consumeTopic, timeout, environment);

        return new String(record.value());
    }
}
