package soya.framework.server;

public interface TraceableEvent {
    EventState getState();

    Object getResult();

    void setResult(Object result);

}
