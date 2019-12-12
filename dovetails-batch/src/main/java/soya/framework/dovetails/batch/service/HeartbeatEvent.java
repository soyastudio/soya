package soya.framework.dovetails.batch.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HeartbeatEvent implements ServiceEvent {
    private static Map<Class<? extends HeartbeatEvent>, Integer> counts = new ConcurrentHashMap<>();

    private long timestamp;

    public HeartbeatEvent() {
        this.timestamp = System.currentTimeMillis();
        Integer count = counts.get(getClass());
        if(count == null) {
            count = new Integer(0);
        }

        counts.put(getClass(), new Integer(count.intValue() + 1));
    }

    public int getCount() {
        return counts.get(getClass());
    }
}
