package soya.framework.commons.cli;

import org.apache.commons.cli.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

public abstract class CommandDispatcher {

    private static CommandLineParser parser = new DefaultParser();
    private Map<String, CommandFactory> methodMappings;

    public CommandDispatcher() {
    }

    private void _init() {
        methodMappings = new HashMap<>();
        CommandExecutionContext context = CommandExecutionContext.getInstance();
        GroupMapping groupMapping = getClass().getAnnotation(GroupMapping.class);
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            CommandMapping mapping = method.getAnnotation(CommandMapping.class);
            if (mapping != null) {
                String uri = groupMapping.value() + "://" + mapping.command();
                if (context.getCommandType(uri) != null) {
                    methodMappings.put(method.getName(), new CommandFactory(context.getCommandType(uri), method));
                }

            }
        }
    }

    protected String _dispatch(String methodName, Object[] args) throws Exception {
        if (methodMappings == null) {
            _init();
        }

        CommandCallable<?> cmd = methodMappings.get(methodName).create(args);
        Future<?> future = CommandExecutionContext.getInstance().getExecutorService().submit(cmd);
        while (!future.isDone()) {
            Thread.sleep(100l);
        }

        Object result = future.get();

        if (result == null) {
            return null;
        } else {
            return result.toString();
        }
    }

    protected Object _dispatch(String uri, String arguments, String message) throws Exception {
        Class<? extends CommandCallable> cls = CommandExecutionContext.getInstance().getCommandType(uri);
        Options options = CommandParser.parse(cls);
        Field[] fields = CommandParser.getOptionFields(cls);

        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(arguments);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.contains("{") && token.contains("}")) {
                token = replace(token, new Object[0]);
            }


            list.add(token);
        }
        if (message != null) {
            list.add(encodeMessage(message));
        }

        CommandCallable cmd = cls.newInstance();
        CommandLine commandLine = new DefaultParser().parse(options, list.toArray(new String[list.size()]));

        for (Field field : fields) {
            field.setAccessible(true);
            CommandOption commandOption = field.getAnnotation(CommandOption.class);

            String value = commandLine.getOptionValue(commandOption.option());
            if (value != null && !"null".equals(value)) {
                field.set(cmd, value);
            }
        }

        Future<?> future = CommandExecutionContext.getInstance().getExecutorService().submit(cmd);
        while (!future.isDone()) {
            Thread.sleep(150l);
        }

        return future.get();

    }

    protected Object _dispatch(URI uri) throws Exception {
        Class<? extends CommandCallable> cls = null;
        String queryString = uri.getQuery();
        String path = uri.getPath();

        if (uri.getScheme().equals("class")) {
            cls = (Class<? extends CommandCallable>) Class.forName(uri.getHost());

        } else {
            String key = uri.getScheme() + "://" + uri.getHost();
            cls = CommandExecutionContext.getInstance().getCommandType(key);

        }

        if (cls != null) {
            Map<String, String> parameters = new HashMap<>();
            if (queryString != null && queryString.trim().length() > 0) {
                for (String pair : queryString.split("&")) {
                    int equals = pair.indexOf('=');
                    if (equals == 0)
                        throw new IllegalArgumentException("invalid query parameter: " + pair);

                    String opt = equals > 0 ? pair.substring(0, equals) : pair;
                    String value = equals > 0 ? pair.substring(equals + 1) : null;

                    parameters.put(opt, value);
                }
            }

            Options options = CommandParser.parse(cls);
            List<String> list = new ArrayList<>();
            options.getOptions().forEach(e -> {
                if (parameters.containsKey(e.getOpt())) {
                    list.add("-" + e.getOpt());
                    list.add(parameters.get(e.getOpt()));

                } else if (parameters.containsKey(e.getLongOpt())) {
                    list.add("--" + e.getLongOpt());
                    list.add(parameters.get(e.getLongOpt()));

                }
            });
            CommandCallable<?> cmd = CommandParser.create(cls, list.toArray(new String[list.size()]));

            Future<?> future = CommandExecutionContext.getInstance().getExecutorService().submit(cmd);
            while (!future.isDone()) {
                Thread.sleep(100l);
            }

            return future.get();

        } else {
            return null;
        }
    }

    protected String encodeMessage(String message) {
        return "base64://" + Base64.getEncoder().encodeToString(message.getBytes());
    }

    private static String replace(String template, Object[] args) {
        StringBuilder builder = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(template, "}");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.contains("{")) {
                int index = token.indexOf("{");
                String firstPart = token.substring(0, index);
                String paramPart = token.substring(index + 1);
                try {
                    Object value = args[Integer.parseInt(paramPart)];
                    paramPart = value.toString();

                } catch (Exception e) {
                    paramPart = CommandExecutionContext.getInstance().getProperty(paramPart);
                }

                token = firstPart + paramPart;

            }

            builder.append(token);
        }

        return builder.toString();
    }

    private static class CommandFactory<T extends CommandCallable> {
        private final Class<T> type;
        private final Method method;

        private Field[] fields;
        private Options options;

        private String template;

        private CommandFactory(Class<T> type, Method method) {
            this.type = type;
            this.method = method;

            this.fields = CommandParser.getOptionFields(type);
            this.options = CommandParser.parse(type);
            template = method.getAnnotation(CommandMapping.class).template();
        }

        public T create(Object[] args) throws Exception {
            T cmd = type.newInstance();
            CommandLine commandLine = parse(template, options, args);

            for (Field field : fields) {
                field.setAccessible(true);
                CommandOption commandOption = field.getAnnotation(CommandOption.class);

                String value = commandLine.getOptionValue(commandOption.option());
                if (value != null && !"null".equals(value)) {
                    field.set(cmd, value);
                }
            }

            return cmd;
        }

        private CommandLine parse(String template, Options options, Object[] args) throws ParseException {
            List<String> list = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(template);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.contains("{") && token.contains("}")) {
                    token = replace(token, args);
                }
                list.add(token);
            }
            return parser.parse(options, list.toArray(new String[list.size()]));
        }

    }
}
