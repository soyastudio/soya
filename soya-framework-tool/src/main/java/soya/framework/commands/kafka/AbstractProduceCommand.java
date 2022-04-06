package soya.framework.commands.kafka;

import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

public abstract class AbstractProduceCommand extends KafkaCommand {

    @CommandOption(option = "p", longOption = "produceTopic", required = true)
    protected String produceTopic;

    @CommandOption(option = "m", longOption = "message", required = true, dataForProcessing = true)
    protected String message;


    protected String getMessage() throws Exception {
        return Resources.getResourceAsString(message);
    }
}
