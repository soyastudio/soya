package soya.framework.dovetails.batch.service;

public interface ServiceEventListener<E extends ServiceEvent> {
    void onEvent(E event);
}
