package soya.framework.commandline.oas;

import soya.framework.commandline.TaskCallable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Dispatcher {
    private static Map<Class<? extends TaskCallable>, Dispatcher> instances = new ConcurrentHashMap();
    private Class<? extends TaskCallable> api;

    private Dispatcher(Class<? extends TaskCallable> api) {
        this.api = api;
    }

    public Dispatcher getInstance(Class<? extends TaskCallable> api) {
        if(!instances.containsKey(api)) {
            instances.put(api, new Dispatcher(api));
        }

        return instances.get(api);
    }

}
