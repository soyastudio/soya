package soya.framework.commands.apache.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

@Command(group = "kafka", name = "pub-and-sub")
public class PubAndSubCommand extends AbstractProduceCommand {

    @CommandOption(option = "c", required = true)
    protected String consumeTopic;

    @Override
    public String call() throws Exception {
        String msg = Resources.getResourceAsString(message);
        ConsumerRecord<String, byte[]> record = KafkaUtils.pubAndSub(consumeTopic, msg, produceTopic, timeout, environment);

        return new String(record.value());
    }
}
