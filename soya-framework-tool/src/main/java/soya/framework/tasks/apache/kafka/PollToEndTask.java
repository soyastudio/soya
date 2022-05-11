package soya.framework.tasks.apache.kafka;

import soya.framework.core.Command;

@Command(group = "kafka", name = "poll-to-end", httpMethod = Command.HttpMethod.GET)
public class PollToEndTask extends KafkaTask {

    @Override
    public String execute() throws Exception {
        return null;
    }
}
