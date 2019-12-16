package soya.framework.dovetails.batch.server;

public interface ServiceEventListener<E extends ServiceEvent> {
    void onEvent(E e);
}
