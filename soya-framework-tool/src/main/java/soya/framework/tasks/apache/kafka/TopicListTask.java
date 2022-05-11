package soya.framework.tasks.apache.kafka;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "kafka", name = "topics", httpMethod = Command.HttpMethod.GET)
public class TopicListTask extends KafkaTask {

    @CommandOption(option = "q")
    private String query;

    @Override
    public String execute() throws Exception {
        return GSON.toJson(KafkaUtils.topics(environment, query));
    }
}
