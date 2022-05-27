package soya.framework.commandline.tasks.kafka;

import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

@Command(group = "kafka", name = "topics", httpMethod = Command.HttpMethod.GET)
public class TopicListTask extends KafkaTask {

    @CommandOption(option = "q")
    private String query;

    @Override
    public String execute() throws Exception {
        return GSON.toJson(KafkaUtils.topics(environment, query));
    }
}
