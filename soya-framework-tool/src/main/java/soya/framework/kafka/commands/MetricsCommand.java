package soya.framework.kafka.commands;

import soya.framework.commons.cli.Command;
import soya.framework.kafka.KafkaUtils;

@Command(group = "kafka", name = "metrics", httpMethod = Command.HttpMethod.GET)
public class MetricsCommand extends KafkaCommand {

    @Override
    public String call() throws Exception {
        return GSON.toJson(KafkaUtils.metrics(environment));
    }
}
