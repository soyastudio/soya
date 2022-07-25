package soya.framework.commons.eventbus;

public interface Subscriber {
    void onEvent(Event event);
}
