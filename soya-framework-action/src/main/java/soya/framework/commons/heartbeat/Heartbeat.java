package soya.framework.commons.heartbeat;

import java.util.*;

public final class Heartbeat {

    private final String name;
    private final long delay;
    private final long period;
    private Set<HeartbeatListener> listeners = new HashSet<>();

    private long count;

    private Heartbeat(String name, long delay, long period, Set<HeartbeatListener> listeners) {
        this.name = name;
        this.period = period;
        this.delay = delay;
        this.listeners.addAll(listeners);

        new Timer(name).schedule(new HeartbeatTask(this), delay, period);
    }

    public void addListener(HeartbeatListener listener) {
        this.listeners.add(listener);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name = "HEARTBEAT";
        private long period = 1000l;
        private long delay;
        private Set<HeartbeatListener> listeners = new HashSet<>();

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder period(long period) {
            this.period = period;
            return this;
        }

        public Builder addListener(HeartbeatListener listener) {
            this.listeners.add(listener);
            return this;
        }

        public Heartbeat create() {
            return new Heartbeat(name, delay, period, listeners);
        }
    }

    public static class HeartbeatEvent extends EventObject {
        private final String name;
        private final long timestamp;
        private final long sequence;

        protected HeartbeatEvent(Heartbeat source) {
            super(source);
            this.name = source.name;
            this.timestamp = System.currentTimeMillis();
            this.sequence = source.count;
        }

        public String getName() {
            return name;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getSequence() {
            return sequence;
        }
    }

    public interface HeartbeatListener {
        void onEvent(HeartbeatEvent event);
    }

    static class HeartbeatTask extends TimerTask {
        private final Heartbeat heartbeat;

        HeartbeatTask(Heartbeat heartbeat) {
            this.heartbeat = heartbeat;
        }

        @Override
        public void run() {
            heartbeat.count++;
            HeartbeatEvent event = new HeartbeatEvent(heartbeat);
            heartbeat.listeners.forEach(e -> {
                ((Runnable) () -> e.onEvent(event)).run();
            });
        }
    }

}
