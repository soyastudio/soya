package soya.framework.tasks.apache.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

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

