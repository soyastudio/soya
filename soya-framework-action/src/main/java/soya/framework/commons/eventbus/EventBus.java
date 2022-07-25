package soya.framework.commons.eventbus;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class EventBus {
    protected static EventBus instance;

    protected Set<Registration> registrations = new HashSet<>();

    protected EventBus() {
        if (instance != null) {
            throw new IllegalStateException("EventBus has already been created.");
        }
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new DefaultEventBus();
        }
        return instance;
    }

    void post(Event event) {
        registrations.forEach(e -> {
            if (event.getType().equals(e.eventType)) {
                dispatch(event, e.subscriber);
            }
        });
    }

    public URI addSubscriber(String uri, String name, Subscriber subscriber) {
        registrations.add(new Registration(uri, name, subscriber));
        return URI.create(uri + "/" + name);
    }

    protected abstract void dispatch(Event event, Subscriber subscriber);

    static class Registration {
        private final String eventType;
        private final String name;
        private final Subscriber subscriber;

        Registration(String eventType, String name, Subscriber subscriber) {
            this.eventType = eventType;
            this.name = name;
            this.subscriber = subscriber;
        }

        public String toString() {
            return eventType + "/" + name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Registration)) return false;
            Registration that = (Registration) o;
            return Objects.equals(eventType, that.eventType) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventType, name);
        }
    }

    public interface EventChannelManager {
        String[] channels();

        String[] subscribers(String channel);

    }

    static class DefaultEventBus extends EventBus implements EventChannelManager {
        private ExecutorService executorService;

        protected DefaultEventBus() {
            super();
            executorService = Executors.newFixedThreadPool(10);
        }

        @Override
        protected void dispatch(Event event, Subscriber subscriber) {
            executorService.execute(() -> {
                subscriber.onEvent(event);
            });
        }

        @Override
        public String[] channels() {
            Set<String> set = new HashSet<>();
            registrations.forEach(e -> {
                set.add(e.eventType);
            });
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }

        @Override
        public String[] subscribers(String channel) {
            Set<String> set = new HashSet<>();
            registrations.forEach(e -> {
                if(e.eventType.equals(channel)) {
                    set.add(e.name);
                }
            });
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }
    }

}
