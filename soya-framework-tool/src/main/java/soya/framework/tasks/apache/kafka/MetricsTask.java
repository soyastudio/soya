package soya.framework.tasks.apache.kafka;

import soya.framework.core.Command;

@Command(group = "kafka", name = "metrics", httpMethod = Command.HttpMethod.GET)
public class MetricsTask extends KafkaTask {

    @Override
    public String execute() throws Exception {
        return GSON.toJson(KafkaUtils.metrics(environment));
    }
}
