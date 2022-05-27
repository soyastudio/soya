package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

@Command(group = "kafka", name = "pub-and-sub")
public class PubAndSubTask extends AbstractProduceTask {

    @CommandOption(option = "c", required = true)
    protected String consumeTopic;

    @Override
    public String execute() throws Exception {
        ConsumerRecord<String, byte[]> record = KafkaUtils.pubAndSub(produceTopic, message, consumeTopic, timeout, environment);
        return new String(record.value());
    }
}
