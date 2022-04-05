package soya.framework.kafka.commands;

import soya.framework.core.CommandOption;

public abstract class AbstractConsumeCommand extends KafkaCommand {

    @CommandOption(option = "c", longOption = "consumeTopic", required = true, paramType = CommandOption.ParamType.PathParam)
    protected String consumeTopic;
}
