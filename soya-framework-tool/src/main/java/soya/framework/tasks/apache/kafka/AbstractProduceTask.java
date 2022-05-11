package soya.framework.tasks.apache.kafka;

import soya.framework.core.CommandOption;

public abstract class AbstractProduceTask extends KafkaTask {

    @CommandOption(option = "p", required = true)
    protected String produceTopic;

    @CommandOption(option = "m", required = true, dataForProcessing = true)
    protected String message;
}
