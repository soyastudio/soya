package soya.framework.tasks.apache.kafka;

import soya.framework.commandline.CommandOption;

public abstract class AbstractConsumeTask extends KafkaTask {

    @CommandOption(option = "c", required = true, paramType = CommandOption.ParamType.PathParam)
    protected String consumeTopic;
}
