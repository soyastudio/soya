package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.commons.eventbus.Event;

import java.net.URI;

@Command(group = "eventbus", name = "deliver", httpMethod = Command.HttpMethod.POST)
public class DeliverEventAction extends EventBusAction<Event> {

    @CommandOption(option = "m", dataForProcessing = true)
    private String payload;

    @Override
    protected Event execute() throws Exception {
        return Event.builder(URI.create(uri), "Test").setPayload(payload).create();
    }
}
