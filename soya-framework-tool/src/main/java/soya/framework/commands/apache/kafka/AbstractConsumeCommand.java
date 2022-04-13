package soya.framework.commands.apache.kafka;

import soya.framework.core.CommandOption;

public abstract class AbstractConsumeCommand extends KafkaCommand {

    @CommandOption(option = "c", required = true, paramType = CommandOption.ParamType.PathParam)
    protected String consumeTopic;
}
