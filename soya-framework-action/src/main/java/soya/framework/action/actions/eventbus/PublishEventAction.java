package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "eventbus", name = "publish", httpMethod = Command.HttpMethod.POST)
public class PublishEventAction extends EventBusAction<String> {

    @CommandOption(option = "m", dataForProcessing = true)
    private String payload;

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
