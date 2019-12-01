package soya.framework.dovetails.support;

import com.google.gson.JsonElement;
import soya.framework.dovetails.ServiceManager;

public abstract class ServiceManagerSupport<T> implements ServiceManager<T> {
    protected T service;

    protected String type;
    protected JsonElement settings;

    @Override
    public T start() {
        this.service = build(type, settings);
        start(service);
        return service;
    }

    @Override
    public void stop() {
        stop(service);
    }

    protected abstract T build(String type, JsonElement settings);

    protected void start(T service) {

    }

    protected void stop(T service) {

    }
}
