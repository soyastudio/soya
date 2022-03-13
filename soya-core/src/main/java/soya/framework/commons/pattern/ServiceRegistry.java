package soya.framework.commons.pattern;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceRegistry {

    private static ServiceRegistry me;
    private ServiceLocatorImpl locator;

    static {
        me = new ServiceRegistry();
    }

    private ServiceRegistry() {
        this.locator = new ServiceLocatorImpl();
        ServiceLocator.Singleton.initialize(locator);
    }

    public static ServiceRegistry getInstance() {
        return me;
    }

    public ServiceRegistry register(String id, Object service) {
        locator.services.put(id, service);
        return this;
    }

    public <T> T getService(String id, Class<T> type) {
        if (locator.services.containsKey(id)) {
            return (T) locator.services.get(id);

        } else {
            throw new ServiceLocator.ServiceLocatorException("Cannot find service with id: " + id);
        }
    }

    static class ServiceLocatorImpl implements ServiceLocator {
        private Map<String, Object> services = new LinkedHashMap<>();

        private ServiceLocatorImpl() {
        }

        @Override
        public <T> T find(Class<T> type) throws ServiceLocatorException {
            for (Object o : services.values()) {
                if (type.isInstance(o)) {
                    return (T) o;
                }
            }

            throw new ServiceLocatorException(type);
        }
    }
}
