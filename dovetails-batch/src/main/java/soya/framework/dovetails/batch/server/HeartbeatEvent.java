package soya.framework.dovetails.batch.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatEvent implements ServiceEvent {
    private static Map<Class<? extends HeartbeatEvent>, Integer> counts = new ConcurrentHashMap<>();

    private final long timestamp;
    private final int sequence;

    protected HeartbeatEvent() {
        timestamp = System.currentTimeMillis();
        Class<? extends HeartbeatEvent> eventType = getClass();
        Integer count = counts.get(eventType);
        if(count == null) {
            count = new Integer(0);
        }
        sequence = count.intValue() + 1;
        counts.put(eventType, new Integer(sequence));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSequence() {
        return sequence;
    }
}
