package com.albertsons.specright.eventbus;

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

    public URI addSubscriber(String uri, Subscriber subscriber) {
        registrations.add(new Registration(uri, subscriber));
        return URI.create(uri);
    }

    protected abstract void dispatch(Event event, Subscriber subscriber);

    static class Registration {
        private final String eventType;
        private final Subscriber subscriber;

        Registration(String eventType, Subscriber subscriber) {
            this.eventType = eventType;
            this.subscriber = subscriber;
        }

        public String toString() {
            return eventType;
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
                    set.add(e.getClass().getName());
                }
            });
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }
    }

    public interface Subscriber {
        void onEvent(Event event);
    }
}
