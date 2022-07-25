package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;

@Command(group = "eventbus", name = "event-channels", httpMethod = Command.HttpMethod.GET)
public class EventChannelsAction extends EventBusAction<String[]> {
    @Override
    protected String[] execute() throws Exception {
        return eventChannelManager().channels();
    }
}
