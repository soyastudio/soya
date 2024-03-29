package soya.framework.action;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import soya.framework.action.actions.reflect.ReflectionAction;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public abstract class ActionContext {

    private static Logger logger = Logger.getLogger(ActionContext.class.getName());
    private static ActionContext INSTANCE;

    private final ExecutorService executorService;
    private final ServiceLocator serviceLocator;

    protected Properties properties;
    protected Map<String, GroupDescription> groupDescriptions;
    protected Set<ActionName> actions;

    protected ActionContext(Properties properties,
                            ExecutorService executorService,
                            ServiceLocator serviceLocator,
                            Set<String> scanPackages) {
        this.properties = properties;
        this.executorService = executorService;
        this.serviceLocator = serviceLocator;
        this.groupDescriptions = new HashMap<>();
        this.actions = new HashSet<>();

        if (scanPackages != null && scanPackages.size() > 0) {
            scan(scanPackages.toArray(new String[scanPackages.size()]));

        } else {
            scan(ReflectionAction.class.getPackage().getName());

        }

        INSTANCE = this;
    }

    private void scan(String... packageName) {
        Reflections scanner = new Reflections(new ConfigurationBuilder()
                .forPackages(packageName)
                .setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated));

        scanner.getTypesAnnotatedWith(Domain.class).forEach(e -> {
            Domain domain = e.getAnnotation(Domain.class);
            if (domain != null) {
                GroupDescription groupDescription = new GroupDescription(domain.group(), domain.title(), domain.description());
                groupDescriptions.put(groupDescription.getGroup(), groupDescription);
            }
        });

        scanner.getTypesAnnotatedWith(Command.class).forEach(e -> {
            actions.add(ActionClass.get((Class<? extends ActionCallable>) e).getActionName());
        });

    }

    public static ActionContext getInstance() {
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

    public boolean containsGroup(String group) {
        return groupDescriptions.containsKey(group);
    }

    public List<String> groups() {
        Set<String> set = new HashSet<>();
        actions.forEach(e -> {
            set.add(e.getGroup());
        });
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return list;
    }

    public GroupDescription groupDescription(String group) {
        return groupDescriptions.get(group);
    }

    public List<ActionName> getCommands() {
        List<ActionName> list = new ArrayList<>(actions);
        Collections.sort(list);
        return list;
    }

    public List<ActionName> getCommands(String group) {
        List<ActionName> list = new ArrayList<>();
        actions.forEach(e -> {
            if (e.getGroup().equals(group)) {
                list.add(e);
            }
        });
        Collections.sort(list);
        return list;
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

    public interface ExecutionContextHandler {
        String[] getRequiredProperties();

        void setRequiredProperty(String propName, String propValue);

    }

    public class ServiceLocatorException extends NullPointerException {
        private Class<?> type;

        public ServiceLocatorException(Class<?> type) {
            super("Cannot find service of type '" + type.getName() + "'!");
        }
    }

    public static class Builder {

        private Properties properties = new Properties();
        private String home;
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

        public Builder setHome(String home) {
            this.home = home;
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

        public ActionContext create() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            return new DefaultExecutionContext(properties, executorService, new DefaultServiceLocator(serviceLocator, services), this.scanPackages);
        }
    }

    static class DefaultExecutionContext extends ActionContext implements ServiceLocator, ExecutionContextHandler {
        protected DefaultExecutionContext(Properties properties, ExecutorService executorService, ServiceLocator serviceLocator, Set<String> scanPackages) {
            super(properties, executorService, serviceLocator, scanPackages);
        }

        @Override
        public List<String> serviceTypes() {
            return getServiceLocator().serviceTypes();
        }

        @Override
        public String[] getRequiredProperties() {
            Set<String> set = new HashSet<>();
            actions.forEach(e -> {
                Field[] fields = ActionClass.get(e).getActionFields();
                for (Field field : fields) {
                    CommandOption commandOption = field.getAnnotation(CommandOption.class);
                    if (commandOption.paramType().equals(CommandOption.ParamType.ReferenceParam)) {
                        String ref = commandOption.referenceKey();
                        set.add(ref);
                    }
                }

            });

            return set.toArray(new String[set.size()]);
        }

        @Override
        public void setRequiredProperty(String propName, String propValue) {
            if(propName != null && propValue != null) {
                properties.setProperty(propName, propValue);
            } else {
                logger.warning("Required property '" + propName + "' is not set.");
            }
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

    static class SpringApplicationContextWrapper implements ServiceLocator {

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
                return null;
            }
        }

        @Override
        public List<String> serviceTypes() {
            try {
                Set<String> set = new HashSet<>();
                String[] names = (String[]) cls.getMethod("getBeanDefinitionNames", new Class[0]).invoke(applicationContext, new Object[0]);
                for (String name : names) {
                    Object obj = cls.getMethod("getBean", new Class[]{String.class}).invoke(applicationContext, new Object[]{name});
                    Class clazz = obj.getClass();
                    String className = clazz.getName();
                    if (!className.startsWith("org.springframework.")
                            && !className.contains("$$EnhancerBySpringCGLIB$$")) {
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
