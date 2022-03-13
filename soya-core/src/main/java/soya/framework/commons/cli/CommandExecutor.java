package soya.framework.commons.cli;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class CommandExecutor {
    private static ExecutorService DEFAULT_EXECUTOR;

    private Context context;
    private CommandFactory commandFactory;
    private ExecutorService executorService;

    private CommandExecutor(ExecutorService executorService, Context context, CommandFactory commandFactory) {
        this.executorService = executorService;
        this.context = context;
        this.commandFactory = commandFactory;
    }

    public Context context() {
        return context;
    }

    public String execute(String commandline) throws Exception {
        CommandCallable command = commandFactory.create(commandline, context);
        Future<String> future = executorService.submit(command);
        while (!future.isDone()) {
            Thread.sleep(100l);
        }

        return future.get();

    }

    public String execute(String cmd, String[] args) throws Exception {
        CommandCallable command = context.create(cmd, args);
        Future<String> future = executorService.submit(command);
        while (!future.isDone()) {
            Thread.sleep(100l);
        }

        return future.get();
    }

    public Future<String> submit(String cmd, String[] args) {
        CommandCallable command = context.create(cmd, args);
        return executorService.submit(command);
    }

    public static String execute(Class<?> cls, String methodName, Object[] args, CommandExecutor delegate) throws Exception {
        Method method = null;
        for (Method m : cls.getMethods()) {
            if (methodName.equals(m.getName())) {
                method = m;
                break;
            }
        }

        if (method == null) {
            throw new IllegalArgumentException("Cannot find method: " + methodName);
        }

        List<String> arguments = new ArrayList<>();
        CommandMapping clt = method.getAnnotation(CommandMapping.class);
        if (clt == null) {
            throw new IllegalArgumentException("Cannot find CommandLineTemplate on method: " + methodName);
        }

        OptionMapping[] optionMappings = clt.options();
        for (OptionMapping optionMapping : optionMappings) {
            String opt = optionMapping.option();
            String value = null;
            if (optionMapping.parameterIndex() >= 0) {
                value = args[optionMapping.parameterIndex()].toString();
            } else if (optionMapping.property() != null && !optionMapping.property().endsWith("")) {
                if (delegate.context().property(optionMapping.property()) != null) {
                    value = delegate.context().property(optionMapping.property());

                } else if (System.getProperty(optionMapping.property()) != null) {
                    value = System.getProperty(optionMapping.property());

                }
            }

            arguments.add("-" + opt);
            arguments.add(value);
        }

        // FIXME:
        if (clt.template() != null && clt.template().trim().length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(clt.template());
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.startsWith("-")) {
                    arguments.add(token);

                } else if (token.startsWith("{{") && token.endsWith("}}")) {
                    String p = token.substring(2, token.length() - 2);
                    try {
                        int index = Integer.parseInt(p);
                        Object value = args[index];
                        if (value != null) {
                            String st = value.toString().trim();
                            StringTokenizer stringTokenizer = new StringTokenizer(st);
                            while (stringTokenizer.hasMoreTokens()) {
                                arguments.add(stringTokenizer.nextToken());
                            }

                        }

                    } catch (NumberFormatException ex) {
                        if (delegate.context().property(p) != null) {
                            arguments.add(delegate.context().property(p));

                        } else if (System.getProperty(p) != null) {
                            arguments.add(System.getProperty(p));

                        }
                    }

                } else {
                    arguments.add(token);
                }
            }

        }

        String cmd = clt.command();
        if (cmd.startsWith("{{") && cmd.endsWith("}}")) {
            cmd = cmd.substring(2, cmd.length() - 2);
            int index = Integer.parseInt(cmd);

            cmd = args[index].toString();
        }

        return delegate.execute(cmd, arguments.toArray(new String[arguments.size()]));
    }

    public static Builder builder(Class<? extends CommandCallable> commandType) {
        return new Builder(commandType);
    }

    public static class Builder {
        private String name;
        private Class<? extends CommandCallable> commandType;
        private Set<Class<? extends CommandCallable>> set = new HashSet<>();
        private Properties properties = new Properties();
        private CommandFactory commandFactory;
        private ExecutorService executorService;
        private Option commandOption;

        private Builder(Class<? extends CommandCallable> commandType) {
            this.commandType = commandType;
            this.name = commandType.getSimpleName();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder add(Class<? extends CommandCallable>... commandTypes) {
            for (Class<? extends CommandCallable> c : commandTypes) {
                if (commandType.isAssignableFrom(c)) {
                    set.add((Class<? extends CommandCallable>) c);
                }
            }
            return this;
        }

        public Builder scan(String packageName) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> subTypes =
                    reflections.getTypesAnnotatedWith(Command.class);
            subTypes.forEach(c -> {
                if (commandType.isAssignableFrom(c)) {
                    set.add((Class<? extends CommandCallable>) c);
                }
            });

            return this;
        }

        public Builder setProperty(String propName, String propValue) {
            properties.setProperty(propName, propValue);
            return this;
        }

        public Builder setProperties(Properties properties) {
            this.properties.putAll(properties);
            return this;
        }

        public Builder setCommandOption(String option, String longOption) {
            this.commandOption = Option.builder(option).longOpt(longOption).desc("Command").required().hasArg().build();
            return this;
        }

        public Builder setCommandFactory(CommandFactory commandFactory) {
            this.commandFactory = commandFactory;
            return this;
        }

        public Builder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public CommandExecutor create() {
            if(commandOption == null) {
                commandOption = Option.builder("a").longOpt("action").hasArg().required().build();
            }

            Context context = new Context(name, set.toArray(new Class[set.size()]), properties, commandOption);
            if (commandFactory == null) {
                commandFactory = new DefaultCommandFactory();
            }

            if (executorService == null) {
                if (DEFAULT_EXECUTOR == null) {
                    DEFAULT_EXECUTOR = Executors.newSingleThreadExecutor();
                }

                executorService = DEFAULT_EXECUTOR;

            }

            return new CommandExecutor(executorService, context, commandFactory);
        }
    }

    public static class Context {

        private String name;
        private Map<String, Class<? extends CommandCallable>> classMap = new LinkedHashMap<>();
        private Map<String, Options> optionsMap = new LinkedHashMap<>();
        private Properties properties = new Properties();
        private Option commandOption;

        Context(String name, Class<? extends CommandCallable>[] classes, Properties properties, Option commandOption) {
            this.name = name;
            for(Class<? extends CommandCallable> c: classes) {
                register((Class<? extends CommandCallable>) c);
            }

            if (properties != null) {
                this.properties = properties;
            }

            this.commandOption = commandOption;
        }

        public String name() {
            return name;
        }

        public Option commandOption() {
            return commandOption;
        }

        public Properties properties() {
            Properties props = new Properties();
            props.putAll(properties);
            return props;
        }

        public String property(String key) {
            return properties.getProperty(key);
        }

        public void setProperty(String key, String value) {
            this.properties.setProperty(key, value);
        }

        public String[] commands() {
            List<String> list = new ArrayList<>(classMap.keySet());
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }

        public Class<? extends CommandCallable> getCommandType(String cmd) {
            return classMap.get(cmd);
        }

        public Options options(String cmd) {
            return optionsMap.get(cmd);
        }

        public CommandCallable create(String cmd, String[] args) {
            if(!classMap.containsKey(cmd)) {
                throw new IllegalArgumentException("Command not defined: " + cmd);
            }
            Class<? extends CommandCallable> cls = classMap.get(cmd);
            Options options = optionsMap.get(cmd);

            try {
                CommandCallable command = cls.newInstance();
                CommandLine commandLine = new DefaultParser().parse(options, args);

                Class<?> superClass = cls;
                while (!Object.class.equals(superClass)) {
                    Field[] fields = superClass.getDeclaredFields();
                    for (Field field : fields) {
                        CommandOption commandOption = field.getAnnotation(CommandOption.class);
                        if (commandOption != null && commandLine.hasOption(commandOption.option())) {
                            String optionValue = commandLine.getOptionValue(commandOption.option());
                            Object value = ConvertUtils.convert(optionValue, field.getType());
                            field.setAccessible(true);
                            field.set(command, value);
                        }
                    }

                    superClass = superClass.getSuperclass();
                }

                return command;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            List<String> list = new ArrayList<>(classMap.keySet());
            Collections.sort(list);

            list.forEach(e -> {
                builder.append(e).append("\n");
                Options options = optionsMap.get(e);
                options.getOptions().forEach(o -> {
                    builder.append("\t-" + o.getOpt()).append("\t--" + o.getLongOpt()).append("\n");
                });
                builder.append("\n");
            });
            return builder.toString();
        }

        public String toString(String cmd) {
            if(cmd != null && classMap.containsKey(cmd)) {
                StringBuilder builder = new StringBuilder();
                builder.append("-a ").append(cmd);
                Options options = optionsMap.get(cmd);
                options.getOptions().forEach(e -> {
                    if(!e.getOpt().equals("r")) {
                        builder.append(" -").append(e.getOpt());
                        if(e.hasArg()) {
                            builder.append(" [");
                            if(e.isRequired()) {
                                builder.append("required: ");
                            } else {
                                builder.append("optional: ");
                            }

                            builder.append(e.getDescription());

                            builder.append("]");
                        }
                    }
                });


                return builder.toString();
            } else {
                return toString();
            }
        }

        private void register(Class<? extends CommandCallable> cls) {
            Command command = cls.getAnnotation(Command.class);
            String name = command.name();
            String uri = command.uri();

            classMap.put(name, cls);
            if(uri != null) {
                classMap.put(uri, cls);
            }

            Options options = new Options();

            Class superClass = cls;
            while (!Object.class.equals(superClass)) {
                Field[] fields = superClass.getDeclaredFields();
                for (Field field : fields) {
                    CommandOption commandOption = field.getAnnotation(CommandOption.class);
                    if (commandOption != null && options.getOption(commandOption.option()) == null) {
                        options.addOption(Option.builder(commandOption.option())
                                .longOpt(commandOption.longOption())
                                .hasArg(commandOption.hasArg())
                                .required(commandOption.required())
                                .desc(commandOption.desc())
                                .build());
                    }
                }
                superClass = superClass.getSuperclass();
            }

            optionsMap.put(name, options);

        }
    }

    static class DefaultCommandFactory implements CommandFactory {

        @Override
        public CommandCallable create(String commandline, Context ctx) {
            String cmdOpt = "-" + ctx.commandOption().getOpt();

            List<String> tokens = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(commandline);
            while (tokenizer.hasMoreTokens()) {
                tokens.add(tokenizer.nextToken());
            }

            String action = null;
            List<String> args = new ArrayList<>();

            int i = 0;
            while (i < tokens.size()) {
                String token = tokens.get(i);
                if (cmdOpt.equals(token)) {
                    i++;
                    action = tokens.get(i);

                } else {
                    args.add(token);

                }

                i++;
            }

            return ctx.create(action, args.toArray(new String[args.size()]));
        }
    }

}
