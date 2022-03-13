package soya.framework.commons.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.*;

public class CommandRunner {

    private static Map<Class<? extends CommandCallable>, Options> cache = new ConcurrentHashMap();

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
        if (!cache.containsKey(cmd)) {
            Options options = parse(cmd);
            cache.put(cmd, options);
        }

        try {
            Options options = cache.get(cmd);
            CommandLine commandLine = new DefaultParser().parse(options, args);

            CommandCallable callable = create(cmd, commandLine);

            return executorService.submit(callable);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static CommandCallable create(Class<? extends CommandCallable> cls, CommandLine commandLine) throws Exception {
        CommandCallable command = cls.newInstance();
        Class<?> superClass = cls;
        while (!Object.class.equals(superClass)) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (commandOption != null && commandLine.hasOption(commandOption.option())) {
                    String value = commandLine.getOptionValue(commandOption.option());
                    field.setAccessible(true);
                    field.set(command, value);
                }
            }

            superClass = superClass.getSuperclass();
        }

        return command;
    }

    static Options parse(Class<? extends CommandCallable> cmd) {
        Command command = cmd.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Command is not defined: " + cmd.getName());
        }

        Options options = new Options();

        Class superClass = cmd;
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

        return options;
    }
}
