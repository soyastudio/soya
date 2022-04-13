package soya.framework.commands.apache.kafka;

import soya.framework.core.Command;

@Command(group = "kafka", name = "poll-to-end", httpMethod = Command.HttpMethod.GET)
public class PollToEndCommand extends KafkaCommand {

    @Override
    public String call() throws Exception {
        return null;
    }
}
