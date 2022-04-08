package soya.framework.oas;

import soya.framework.core.CommandCallable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Dispatcher {
    private static Map<Class<? extends CommandCallable>, Dispatcher> instances = new ConcurrentHashMap();
    private Class<? extends CommandCallable> api;

    private Dispatcher(Class<? extends CommandCallable> api) {
        this.api = api;
    }

    public Dispatcher getInstance(Class<? extends CommandCallable> api) {
        if(!instances.containsKey(api)) {
            instances.put(api, new Dispatcher(api));
        }

        return instances.get(api);
    }

}
