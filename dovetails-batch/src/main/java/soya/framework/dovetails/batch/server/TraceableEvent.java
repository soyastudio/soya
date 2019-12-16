package soya.framework.dovetails.batch.server;

import java.util.UUID;

public abstract class TraceableEvent implements ServiceEvent {
    private final String id;
    private final long startTime;
    private long entTime;

    public TraceableEvent() {
        this.id = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEntTime() {
        return entTime;
    }

    public void close() {
        this.entTime = System.currentTimeMillis();
    }
}
