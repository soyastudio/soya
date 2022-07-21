package soya.framework.server;

public interface ServiceEventListener<E extends ServiceEvent> {
    void onEvent(E e);
}
