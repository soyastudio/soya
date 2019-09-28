package soya.framework.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class DefaultExceptionHandler implements ExceptionHandler<Throwable> {

    private final ImmutableMap<Class<? extends Throwable>, ExceptionHandler> handlers;

    private DefaultExceptionHandler(Map<Class<? extends Throwable>, ExceptionHandler> handlers) {
        this.handlers = ImmutableMap.copyOf(handlers);
    }

    @Override
    public boolean onException(Throwable e) {
        Class<?> exceptionType = e.getClass();
        while (!handlers.containsKey(exceptionType)) {
            if (exceptionType.equals(Object.class)) {
                break;
            }
            exceptionType = exceptionType.getSuperclass();
        }

        if (handlers.containsKey(exceptionType)) {
            return handlers.get(exceptionType).onException(e);
        } else {
            return false;
        }
    }

    public static ExceptionHandlerBuilder builder() {
        return new ExceptionHandlerBuilder();
    }

    public static class ExceptionHandlerBuilder {
        private Map<Class<? extends Throwable>, ExceptionHandler> handlers = new HashMap<>();

        private ExceptionHandlerBuilder() {
        }

        public <T extends Throwable> ExceptionHandlerBuilder addHandler(Class<T> exceptionType, ExceptionHandler<T> handler) {
            handlers.put(exceptionType, handler);
            return this;
        }

        public DefaultExceptionHandler create() {
            return new DefaultExceptionHandler(handlers);
        }
    }
}
