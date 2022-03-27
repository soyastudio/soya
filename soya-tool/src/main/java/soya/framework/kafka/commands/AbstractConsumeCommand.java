package soya.framework.kafka.commands;

import soya.framework.commons.cli.CommandOption;

public abstract class AbstractConsumeCommand extends KafkaCommand {

    @CommandOption(option = "c", longOption = "topic", required = true)
    private String consumeTopic;
}
