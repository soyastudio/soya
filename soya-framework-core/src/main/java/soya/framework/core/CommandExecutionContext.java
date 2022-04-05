package soya.framework.core;

import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CommandExecutionContext {

    private static CommandExecutionContext INSTANCE;

    private final Properties properties;
    private final ExecutorService executorService;
    private final Map<String, Class<? extends CommandCallable>> commands;

    protected CommandExecutionContext(Properties properties, ExecutorService executorService) {
        this.properties = properties;
        this.executorService = executorService;

        this.commands = new HashMap<>();
        Reflections reflections = new Reflections();
        Set<Class<?>> subTypes =
                reflections.getTypesAnnotatedWith(Command.class);
        subTypes.forEach(c -> {
            Command command = c.getAnnotation(Command.class);
            if (command != null) {
                String uri = command.group() + "://" + command.name();
                commands.put(uri, (Class<? extends CommandCallable>) c);
            }
        });

        INSTANCE = this;
    }

    public static CommandExecutionContext getInstance() {
        if (INSTANCE == null) {
            builder().create();
        }
        return INSTANCE;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(this.properties);
        return properties;
    }

    public String getProperty(String prop) {
        return properties.getProperty(prop) == null ? System.getProperty(prop) : properties.getProperty(prop);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public abstract <T> T getService(Class<T> type);

    public List<String> groups() {
        Set<String> set = new HashSet<>();
        commands.keySet().forEach(e -> {
            try {
                set.add(new URI(e).getScheme());
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return list;
    }

    public List<String> getCommands() {
        List<String> list = new ArrayList<>(commands.keySet());
        Collections.sort(list);
        return list;
    }

    public List<String> getCommands(String group) {
        String prefix = group + "://";
        List<String> list = new ArrayList<>();
        commands.entrySet().forEach(e -> {
            String uri = e.getKey();
            if (uri.startsWith(prefix)) {
                list.add(uri);
            }
        });
        Collections.sort(list);
        return list;
    }

    public Class<? extends CommandCallable> getCommandType(String uri) {
        return commands.get(uri);
    }

    public Listable listable() {
        return null;
    }

    public static Builder builder() {
        if (INSTANCE != null) {
            throw new IllegalStateException("CommandExecutionContext already created");
        }

        return new Builder();
    }

    public interface ServiceLocator {
        <T> T getService(Class<T> type);
    }

    public static class Builder {
        private Properties properties = new Properties();
        private ExecutorService executorService;
        private Set<String> scanPackages = new HashSet<>();
        private ServiceLocator serviceLocator;
        private Map<Class<?>, Object> services = new HashMap<>();

        private Builder() {
        }

        public Builder setProperties(Properties properties) {
            this.properties.putAll(properties);
            return this;
        }

        public Builder setProperty(String propName, String propValue) {
            this.properties.setProperty(propName, propValue);
            return this;
        }

        public Builder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder addScanPackages(String... packageNames) {
            for (String pkg : packageNames) {
                scanPackages.add(pkg);
            }
            return this;
        }

        public Builder serviceLocator(Object serviceLocator) {
            if (serviceLocator instanceof ServiceLocator) {
                this.serviceLocator = (ServiceLocator) serviceLocator;

            } else if (isSpringApplicationContext(serviceLocator)) {
                this.serviceLocator = new SpringApplicationContextWrapper(serviceLocator);

            } else {
                throw new IllegalArgumentException("");
            }

            return this;
        }

        private boolean isSpringApplicationContext(Object serviceLocator) {
            try {
                Class c = Class.forName("org.springframework.context.ApplicationContext");
                return c.isInstance(serviceLocator);

            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        public <T> Builder register(T t, Class<T> type) {
            if (type.getName().equals(""))
                services.put(type, t);
            return this;
        }

        public CommandExecutionContext create() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }
            return new DefaultExecutionContext(properties, executorService, new DefaultServiceLocator(serviceLocator, services));
        }
    }

    public interface Listable {
        String[] getServiceDefinitionNames();

        String[] getServiceNamesForType(@Nullable Class<?> var1);

        String[] getServiceNamesForType(@Nullable Class<?> var1, boolean var2, boolean var3);

        <T> Map<String, T> getServicesOfType(@Nullable Class<T> var1) throws Exception;

        <T> Map<String, T> getServicesOfType(@Nullable Class<T> var1, boolean var2, boolean var3) throws Exception;

        String[] getServiceNamesForAnnotation(Class<? extends Annotation> var1);

        Map<String, Object> getServicesWithAnnotation(Class<? extends Annotation> var1) throws Exception;

        @Nullable
        <A extends Annotation> A findAnnotationOnService(String var1, Class<A> var2) throws Exception;

        @Nullable
        <A extends Annotation> A findAnnotationOnService(String var1, Class<A> var2, boolean var3) throws Exception;
    }

    static class DefaultExecutionContext extends CommandExecutionContext implements ServiceLocator {
        private ServiceLocator serviceLocator;

        protected DefaultExecutionContext(Properties properties, ExecutorService executorService, ServiceLocator serviceLocator) {
            super(properties, executorService);
            this.serviceLocator = serviceLocator;
        }


        @Override
        public <T> T getService(Class<T> type) {
            return serviceLocator.getService(type);
        }

        @Override
        public Listable listable() {
            return ((DefaultServiceLocator)serviceLocator).listable();
        }
    }

    static class DefaultServiceLocator implements ServiceLocator {
        private ServiceLocator serviceLocator;
        private Map<Class<?>, Object> services = new HashMap<>();

        public DefaultServiceLocator(ServiceLocator serviceLocator, Map<Class<?>, Object> services) {
            this.serviceLocator = serviceLocator;
            this.services = services;
        }

        @Override
        public <T> T getService(Class<T> type) {
            if (services.containsKey(type)) {
                return (T) services.get(type);

            } else if (serviceLocator != null) {
                return serviceLocator.getService(type);
            }

            return null;
        }

        public Listable listable() {
            return (Listable) serviceLocator;
        }
    }

    static class SpringApplicationContextWrapper implements ServiceLocator, Listable {
        private Object applicationContext;

        SpringApplicationContextWrapper(Object applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public <T> T getService(Class<T> type) {
            try {
                return (T) applicationContext
                        .getClass()
                        .getMethod("getBean", new Class[]{Class.class})
                        .invoke(applicationContext, new Object[]{type});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String[] getServiceDefinitionNames() {
            try {
                return (String[]) applicationContext
                        .getClass()
                        .getMethod("getBeanDefinitionNames", new Class[0])
                        .invoke(applicationContext, new Object[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String[] getServiceNamesForType(@Nullable Class<?> var1) {
            return new String[0];
        }

        @Override
        public String[] getServiceNamesForType(@Nullable Class<?> var1, boolean var2, boolean var3) {
            return new String[0];
        }

        @Override
        public <T> Map<String, T> getServicesOfType(@Nullable Class<T> var1) throws Exception {
            return null;
        }

        @Override
        public <T> Map<String, T> getServicesOfType(@Nullable Class<T> var1, boolean var2, boolean var3) throws Exception {
            return null;
        }

        @Override
        public String[] getServiceNamesForAnnotation(Class<? extends Annotation> var1) {
            return new String[0];
        }

        @Override
        public Map<String, Object> getServicesWithAnnotation(Class<? extends Annotation> var1) throws Exception {
            return null;
        }

        @Nullable
        @Override
        public <A extends Annotation> A findAnnotationOnService(String var1, Class<A> var2) throws Exception {
            return null;
        }

        @Nullable
        @Override
        public <A extends Annotation> A findAnnotationOnService(String var1, Class<A> var2, boolean var3) throws Exception {
            return null;
        }
    }
}
