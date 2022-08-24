package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "eventbus", name = "create-subscriber", httpMethod = Command.HttpMethod.POST)
public class CreateSubscriberAction extends EventBusAction<String> {

    @CommandOption(option = "n", required = true)
    private String name;

    @CommandOption(option = "p", dataForProcessing = true)
    private String pipeline;

    @Override
    protected String execute() throws Exception {

        return null;
    }
}
