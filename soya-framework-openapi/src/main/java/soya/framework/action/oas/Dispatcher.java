package soya.framework.action.oas;

import soya.framework.action.ActionCallable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Dispatcher {
    private static Map<Class<? extends ActionCallable>, Dispatcher> instances = new ConcurrentHashMap();
    private Class<? extends ActionCallable> api;

    private Dispatcher(Class<? extends ActionCallable> api) {
        this.api = api;
    }

    public Dispatcher getInstance(Class<? extends ActionCallable> api) {
        if(!instances.containsKey(api)) {
            instances.put(api, new Dispatcher(api));
        }

        return instances.get(api);
    }

}
