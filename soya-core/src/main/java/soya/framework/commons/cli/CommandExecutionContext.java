package soya.framework.commons.cli;

import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandExecutionContext {
    private static CommandExecutionContext INSTANCE;

    private final Properties properties;
    private final ExecutorService executorService;
    private final Map<Class<?>, Object> services;
    private final Map<String, Class<? extends CommandCallable>> commands;

    private CommandExecutionContext(Properties properties, ExecutorService executorService, Map<Class<?>, Object> services) {
        this.properties = properties;
        this.executorService = executorService;
        this.services = services;

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

    public <T> T getService(Class<T> type) {
        return (T) services.get(type);
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

    public static class Builder {
        private Properties properties = new Properties();
        private ExecutorService executorService;
        private Set<String> scanPackages = new HashSet<>();
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

        public <T> Builder register(T t, Class<T> type) {
            services.put(type, t);
            return this;
        }

        public CommandExecutionContext create() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            return new CommandExecutionContext(properties, executorService, services);
        }
    }
}
