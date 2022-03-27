package soya.framework.commons.cli;

import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandExecutionContext {
    private static CommandExecutionContext me;

    private Properties properties;
    private ExecutorService executorService;
    private Map<String, Class<? extends CommandCallable>> commands = new HashMap<>();

    private CommandExecutionContext(Properties properties, ExecutorService executorService) {
        this.properties = properties;
        this.executorService = executorService;
    }

    public static CommandExecutionContext getInstance() {
        if(me == null) {
            me = builder().create();
        }

        return me;
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
            if(uri.startsWith(prefix)) {
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
        if (me != null) {
            throw new IllegalStateException("CommandExecutionContext already created");
        }

        return new Builder();
    }

    public static class Builder {
        private Properties properties = new Properties();
        private ExecutorService executorService;
        private Set<String> scanPackages = new HashSet<>();

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
            for(String pkg: packageNames) {
                scanPackages.add(pkg);
            }
            return this;
        }

        public CommandExecutionContext create() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            me = new CommandExecutionContext(properties, executorService);

            Reflections reflections = new Reflections();
            Set<Class<?>> subTypes =
                    reflections.getTypesAnnotatedWith(Command.class);
            subTypes.forEach(c -> {
                Command command = c.getAnnotation(Command.class);
                if (command != null) {
                    String uri = command.group() + "://" + command.name();
                    me.commands.put(uri, (Class<? extends CommandCallable>) c);
                }
            });

            return me;
        }
    }
}
