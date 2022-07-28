package soya.framework.commons.eventbus;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class EventBus {
    private static EventBus instance;

    protected Set<Registration> registrations = new HashSet<>();

    protected EventBus() {
        if (instance != null) {
            throw new IllegalStateException("EventBus has already been created.");
        }

        instance = this;
    }

    private static EventBus getInstance() {
        if (instance == null) {
            instance = new DefaultEventBus();
        }
        return instance;
    }

    static void publish(Event event) {
        getInstance().post(event);
    }

    public static URI subscribe(String uri, Subscriber subscriber) {
        return getInstance().addSubscriber(uri, subscriber);
    }

    public static EventChannelManager channelManager() {
        return (getInstance() instanceof EventChannelManager) ? (EventChannelManager) instance : null;
    }

    private void post(Event event) {
        registrations.forEach(e -> {
            if (event.getAddress().equals(e.uri)) {
                dispatch(event, e.subscriber);
            }
        });
    }

    private URI addSubscriber(String uri, Subscriber subscriber) {
        registrations.add(new Registration(uri, subscriber));
        return URI.create(uri);
    }

    protected abstract void dispatch(Event event, Subscriber subscriber);

    static class Registration {
        private final String uri;
        private final Subscriber subscriber;

        Registration(String eventType, Subscriber subscriber) {
            this.uri = eventType;
            this.subscriber = subscriber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Registration)) return false;
            Registration that = (Registration) o;
            return Objects.equals(uri, that.uri)
                    && Objects.equals(subscriber.getClass().getName(), that.subscriber.getClass().getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, subscriber.getClass().getName());
        }

        public String toString() {
            return uri + "/" + subscriber.getClass().getName();
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
                set.add(e.uri);
            });
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }

        @Override
        public String[] subscribers(String channel) {
            Set<String> set = new HashSet<>();
            registrations.forEach(e -> {
                if (e.uri.equals(channel)) {
                    set.add(e.getClass().getName());
                }
            });
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }
    }
}
