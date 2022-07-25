package soya.framework.action.actions.eventbus;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.commons.eventbus.EventBus;
import soya.framework.commons.eventbus.Subscriber;

@Command(group = "eventbus", name = "subscribe", httpMethod = Command.HttpMethod.POST)
public class SubscribeAction extends EventBusAction<String> {

    @CommandOption(option = "n", required = true)
    private String name;

    @CommandOption(option = "t", required = true)
    private String type;

    @Override
    protected String execute() throws Exception {
        Subscriber subscriber = null;
        /*if(type.contains("://")) {
            if(type)


        } else {


        }*/

        Class<? extends Subscriber> cls = (Class<? extends Subscriber>) Class.forName(type);
        subscriber = cls.newInstance();

        return EventBus.getInstance().addSubscriber(uri, name, subscriber).toString();
    }
}
