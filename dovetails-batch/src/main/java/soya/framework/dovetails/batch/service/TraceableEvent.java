package soya.framework.dovetails.batch.service;

import java.util.UUID;

public class TraceableEvent implements ServiceEvent {
    private String id;
    private long startTime;
    private long entTime;

    public TraceableEvent() {
        this.id = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
    }

}
