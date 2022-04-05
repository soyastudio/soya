package soya.framework.kafka.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "topics", httpMethod = Command.HttpMethod.GET)
public class TopicListCommand extends KafkaCommand {

    @CommandOption(option = "q", longOption = "query")
    private String query;

    @Override
    public String call() throws Exception {
        return GSON.toJson(KafkaUtils.topics(environment, query));
    }
}
