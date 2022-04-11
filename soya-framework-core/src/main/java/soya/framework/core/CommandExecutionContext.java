package soya.framework.core;

import org.reflections.Reflections;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CommandExecutionContext {

    private static CommandExecutionContext INSTANCE;

    private final Properties properties;
    private final ExecutorService executorService;
    private final ServiceLocator serviceLocator;

    private Map<String, GroupDescription> groupDescriptions = new HashMap<>();
    private Map<String, Class<? extends CommandCallable>> commands = new HashMap<>();

    protected CommandExecutionContext(Properties properties,
                                      ExecutorService executorService,
                                      ServiceLocator serviceLocator,
                                      Set<String> scanPackages) {

        this.properties = properties;
        this.executorService = executorService;
        this.serviceLocator = serviceLocator;
        this.commands = new HashMap<>();

        if (scanPackages != null && scanPackages.size() > 0) {
            scanPackages.forEach(pkg -> {
                Reflections scanner = new Reflections(pkg);
                scanner.getTypesAnnotatedWith(CommandGroup.class).forEach(e -> {
                    CommandGroup commandGroup = e.getAnnotation(CommandGroup.class);
                    if (commandGroup != null) {
                        GroupDescription groupDescription = new GroupDescription(commandGroup.group(), commandGroup.title(), commandGroup.description());
                        groupDescriptions.put(groupDescription.getGroup(), groupDescription);

                    }
                });

                scanner.getTypesAnnotatedWith(Command.class).forEach(e -> {
                    Command command = e.getAnnotation(Command.class);
                    if (command != null) {
                        String uri = command.group() + "://" + command.name();
                        commands.put(uri, (Class<? extends CommandCallable>) e);
                    }
                });
            });

        } else {
            Reflections scanner = new Reflections();
            scanner.getTypesAnnotatedWith(CommandGroup.class).forEach(e -> {
                CommandGroup commandGroup = e.getAnnotation(CommandGroup.class);
                if (commandGroup != null) {
                    GroupDescription groupDescription = new GroupDescription(commandGroup.group(), commandGroup.title(), commandGroup.description());
                    groupDescriptions.put(groupDescription.getGroup(), groupDescription);
                }
            });

            scanner.getTypesAnnotatedWith(Command.class).forEach(e -> {
                Command command = e.getAnnotation(Command.class);
                if (command != null) {
                    String uri = command.group() + "://" + command.name();
                    commands.put(uri, (Class<? extends CommandCallable>) e);
                }
            });

        }

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

    public <T> T getService(Class<T> type) {
        return serviceLocator.getService(type);
    }

    protected ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

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

    public GroupDescription groupDescription(String group) {
        return groupDescriptions.get(group);
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

    public static Builder builder() {
        if (INSTANCE != null) {
            throw new IllegalStateException("CommandExecutionContext already created");
        }

        return new Builder();
    }

    public static final class GroupDescription {
        private final String group;
        private final String title;
        private final String description;

        private GroupDescription(String group, String title, String description) {
            this.group = group;
            this.title = title;
            this.description = description;
        }

        public String getGroup() {
            return group;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public interface ServiceLocator {
        <T> T getService(Class<T> type);

        List<String> serviceTypes();
    }

    public class ServiceLocatorException extends NullPointerException {
        private Class<?> type;

        public ServiceLocatorException(Class<?> type) {
            super("Cannot find service of type '" + type.getName() + "'!");
        }
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

            } else if (SpringApplicationContextWrapper.isApplicationContext(serviceLocator)) {
                this.serviceLocator = new SpringApplicationContextWrapper(serviceLocator);

            } else {
                throw new IllegalArgumentException("Cannot bind service locator with type: " + serviceLocator.getClass().getName());
            }

            return this;
        }

        public <T> Builder register(T t, Class<T> type) {
            services.put(type, t);

            return this;
        }

        public CommandExecutionContext create() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            return new DefaultExecutionContext(properties, executorService, new DefaultServiceLocator(serviceLocator, services), this.scanPackages);
        }
    }

    static class DefaultExecutionContext extends CommandExecutionContext implements ServiceLocator {
        protected DefaultExecutionContext(Properties properties, ExecutorService executorService, ServiceLocator serviceLocator, Set<String> scanPackages) {
            super(properties, executorService, serviceLocator, scanPackages);
        }

        @Override
        public List<String> serviceTypes() {
            return getServiceLocator().serviceTypes();
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

            } else {
                return null;
            }
        }

        @Override
        public List<String> serviceTypes() {
            Set<String> set = new HashSet<>();
            if (serviceLocator != null) {
                set.addAll(serviceLocator.serviceTypes());
            }

            services.keySet().forEach(e -> {
                set.add(e.getName());
            });

            return new ArrayList<>(set);
        }


    }

    static class SpringApplicationContextWrapper implements CommandExecutionContext.ServiceLocator {

        private static Class<?> cls;
        private Object applicationContext;

        static {
            try {
                cls = Class.forName("org.springframework.context.ApplicationContext");
            } catch (ClassNotFoundException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        public SpringApplicationContextWrapper(Object applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public <T> T getService(Class<T> type) {
            try {
                return (T) applicationContext.getClass().getMethod("getBean", new Class[]{Class.class}).invoke(applicationContext, new Object[]{type});

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<String> serviceTypes() {
            try {
                Set<String> set = new HashSet<>();
                String[] names = (String[]) cls.getMethod("getBeanDefinitionNames", new Class[0]).invoke(applicationContext, new Object[0]);
                for (String name : names) {
                    Object obj = cls.getMethod("getBean", new Class[]{String.class}).invoke(applicationContext, new Object[]{name});
                    String className = obj.getClass().getName();
                    if (!className.startsWith("org.springframework.") && !className.contains("$$EnhancerBySpringCGLIB$$")) {
                        set.add(className);
                    }
                }

                return new ArrayList<>(set);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        public static boolean isApplicationContext(Object applicationContext) {
            return cls.isInstance(applicationContext);
        }
    }

}
