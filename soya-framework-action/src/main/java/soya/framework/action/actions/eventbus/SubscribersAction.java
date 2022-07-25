package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;

@Command(group = "eventbus", name = "subscribers", httpMethod = Command.HttpMethod.GET)
public class SubscribersAction extends EventBusAction<String[]> {

    @Override
    protected String[] execute() throws Exception {
        return eventChannelManager().subscribers(uri);
    }
}
