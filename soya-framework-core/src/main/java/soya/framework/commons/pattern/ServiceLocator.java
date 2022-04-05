package soya.framework.commons.pattern;

public interface ServiceLocator {

    <T> T find(Class<T> type) throws ServiceLocatorException;

    class ServiceLocatorException extends RuntimeException {

        public ServiceLocatorException(Class<?> type) {
            super("Cannot find service of type: " + type.getName());
        }


        public ServiceLocatorException(String message) {
            super(message);
        }
    }

    static ServiceLocator singletonInstance() {
        return Singleton.getInstance();
    }

    class Singleton implements ServiceLocator {
        private static Singleton me;
        private ServiceLocator serviceLocator;

        private Singleton(ServiceLocator serviceLocator) {
            this.serviceLocator = serviceLocator;
        }

        private static ServiceLocator getInstance() {
            return me;
        }

        public static ServiceLocator initialize(ServiceLocator serviceLocator) {
            if (me == null) {
                me = new Singleton(serviceLocator);
                return me;
            } else {
                throw new IllegalStateException("Singleton instance already created.");
            }
        }

        @Override
        public <T> T find(Class<T> type) throws ServiceLocatorException {
            return serviceLocator.find(type);
        }
    }
}
