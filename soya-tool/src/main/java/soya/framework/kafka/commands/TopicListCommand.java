package soya.framework.kafka.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "topics")
public class TopicListCommand extends KafkaCommand {

    @CommandOption(option = "q", longOption = "query")
    private String query;

    @Override
    public String call() throws Exception {
        return GSON.toJson(KafkaUtils.topics(environment, query));
    }
}
