package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "eventbus", name = "create-trigger", httpMethod = Command.HttpMethod.POST)
public class CreateTriggerAction extends EventBusAction<String> {

    @CommandOption(option = "n", required = true)
    private String name;

    @CommandOption(option = "m", dataForProcessing = true)
    private String mapping;

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
