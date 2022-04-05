package soya.framework.commons.cli;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandRunner {

    public static String execute(Class<? extends CommandCallable> cmd, String[] args) {
        Future<String> future = execute(cmd, args, Executors.newSingleThreadExecutor());
        while (!future.isDone()) {
            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return future.get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Future<String> execute(Class<? extends CommandCallable> cmd, String[] args, ExecutorService executorService) {

        try {
            CommandCallable callable = CommandParser.create(cmd, args);
            return executorService.submit(callable);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Object execute(URI uri) throws Exception {
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
            if ("/help".equalsIgnoreCase(path)) {
                return help(cls);

            } else {
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

                Future<?> future = execute(cls, list.toArray(new String[list.size()]), Executors.newSingleThreadExecutor());
                while (!future.isDone()) {
                    Thread.sleep(100l);
                }

                return future.get();
            }

        } else {
            return null;
        }

    }

    private static String help(Class<? extends CommandCallable> cls) {
        Command command = cls.getAnnotation(Command.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group", command.group());
        jsonObject.addProperty("name", command.name());
        jsonObject.addProperty("description", command.desc());

        JsonArray array = new JsonArray();

        Class<?> superClass = cls;
        while (!Object.class.equals(superClass)) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (commandOption != null) {
                    JsonObject option = new JsonObject();
                    option.addProperty("option", commandOption.option());
                    option.addProperty("longOption", commandOption.longOption());
                    option.addProperty("required", commandOption.required());
                    option.addProperty("hasArg", commandOption.hasArg());
                    option.addProperty("defaultValue", commandOption.defaultValue());
                    option.addProperty("description", commandOption.desc());

                    array.add(option);
                }
            }

            superClass = superClass.getSuperclass();
        }

        jsonObject.add("options", array);

        return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);

    }
}
