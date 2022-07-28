package soya.framework.action.actions.eventbus;

import soya.framework.action.Action;
import soya.framework.action.CommandGroup;
import soya.framework.action.CommandOption;
import soya.framework.commons.eventbus.EventBus;

import java.lang.reflect.Field;

@CommandGroup(group = "eventbus", title = "Event Bus", description = "Commands for eventbus actions.")
public abstract class EventBusAction<T> extends Action<T> {

    @CommandOption(option = "u", required = true)
    protected String uri;

    protected EventBus.EventChannelManager eventChannelManager() {
        return EventBus.channelManager();
    }
}
