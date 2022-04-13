package soya.framework.commands.apache.kafka;

import soya.framework.core.Command;

@Command(group = "kafka", name = "metrics", httpMethod = Command.HttpMethod.GET)
public class MetricsCommand extends KafkaCommand {

    @Override
    public String call() throws Exception {
        return GSON.toJson(KafkaUtils.metrics(environment));
    }
}
