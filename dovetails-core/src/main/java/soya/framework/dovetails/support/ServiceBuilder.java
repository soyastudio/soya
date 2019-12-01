package soya.framework.dovetails.support;

import java.lang.reflect.ParameterizedType;

public abstract class ServiceBuilder<T> {
    protected Class<T> serviceType;

    private T service;

    public ServiceBuilder() {
        serviceType = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public ServiceBuilder newServiceInstance() {
        return this;
    }

    public ServiceBuilder newServiceInstance(Class<?>[] parameterTypes, Object[] parameters) {
        return this;
    }


}
