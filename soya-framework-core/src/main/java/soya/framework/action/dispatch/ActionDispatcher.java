package soya.framework.action.dispatch;

import org.apache.commons.cli.*;
import soya.framework.action.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

public abstract class ActionDispatcher {

    private static CommandLineParser parser = new DefaultParser();
    private Map<String, CommandFactory> methodMappings;

    public ActionDispatcher() {
    }

    private void _init() {

    }

    protected String _dispatch(String methodName, Object[] args) throws Exception {
        if (methodMappings == null) {
            _init();
        }

        ActionCallable cmd = methodMappings.get(methodName).create(args);
        Future<?> future = ActionContext.getInstance().getExecutorService().submit(cmd);
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
        Class<? extends ActionCallable> cls = ActionContext.getInstance().getActionType(ActionName.fromURI(uri));
        Options options = ActionParser.parse(cls);
        Field[] fields = ActionParser.getOptionFields(cls);

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

        ActionCallable cmd = cls.newInstance();
        CommandLine commandLine = new DefaultParser().parse(options, list.toArray(new String[list.size()]));

        for (Field field : fields) {
            field.setAccessible(true);
            CommandOption commandOption = field.getAnnotation(CommandOption.class);

            String value = commandLine.getOptionValue(commandOption.option());
            if (value != null && !"null".equals(value)) {
                field.set(cmd, value);
            }
        }

        Future<?> future = ActionContext.getInstance().getExecutorService().submit(cmd);
        while (!future.isDone()) {
            Thread.sleep(150l);
        }

        return future.get();

    }

    protected Object _dispatch(URI uri) throws Exception {
        Class<? extends ActionCallable> cls = null;
        String queryString = uri.getQuery();
        String path = uri.getPath();

        if (uri.getScheme().equals("class")) {
            cls = (Class<? extends ActionCallable>) Class.forName(uri.getHost());

        } else {
            String key = uri.getScheme() + "://" + uri.getHost();
            cls = ActionContext.getInstance().getActionType(ActionName.fromURI(key));

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

            Options options = ActionParser.parse(cls);
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
            ActionCallable cmd = ActionParser.create(cls, list.toArray(new String[list.size()]));

            Future<?> future = ActionContext.getInstance().getExecutorService().submit(cmd);
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
                    paramPart = ActionContext.getInstance().getProperty(paramPart);
                }

                token = firstPart + paramPart;

            }

            builder.append(token);
        }

        return builder.toString();
    }

    private static class CommandFactory<T extends ActionCallable> {
        private final Class<T> type;
        private final Method method;

        private Field[] fields;
        private Options options;

        private String template;

        private CommandFactory(Class<T> type, Method method) {
            this.type = type;
            this.method = method;

            this.fields = ActionParser.getOptionFields(type);
            this.options = ActionParser.parse(type);
            template = method.getAnnotation(ActionForward.class).command();
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

    //
    protected Object forward(Method method, Object[] args) {
        ActionForward actionForward = method.getAnnotation(ActionForward.class);
        Class<?> returnType = method.getReturnType();
        String cmd = actionForward.command();
        ActionCallable action = compile(cmd, args);

        return execute(action, returnType);
    }

    private <T> T execute(ActionCallable action, Class<T> destType) throws ActionDispatchException {
        try {
            ActionResult result = action.call();

            return (T) result.result();
        } catch (Exception e) {
            throw new ActionDispatchException(e);
        }
    }

    private ActionCallable compile(String cmd, Object[] args) throws ActionDispatchException {
        String uri = cmd.trim();
        int index = uri.indexOf(32);
        if(index > 0) {
            uri = uri.substring(0, index);
        }

        Class<? extends ActionCallable> type = ActionContext.getInstance().getActionType(ActionName.fromURI(uri));


        throw new ActionDispatchException();
    }


}
