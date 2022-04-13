package soya.framework.commands.apache.kafka;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "kafka", name = "topics", httpMethod = Command.HttpMethod.GET)
public class TopicListCommand extends KafkaCommand {

    @CommandOption(option = "q")
    private String query;

    @Override
    public String call() throws Exception {
        return GSON.toJson(KafkaUtils.topics(environment, query));
    }
}
