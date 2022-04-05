package soya.framework.albertsons.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import soya.framework.core.CommandCallable;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandRunner {
    private static CommandRunner instance;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static Map<String, Class<? extends CommandCallable>> classMap;
    private static Map<String, Options> optionsMap;

    static {
        instance = new CommandRunner(Executors.newSingleThreadExecutor());
        classMap = new LinkedHashMap<>();
        optionsMap = new LinkedHashMap<>();

        register(HelpCommand.class);

        register(XmlSchemaCommand.class);
        register(SampleXmlCommand.class);
        register(XsdToAvscCommand.class);
        register(SampleAvroCommand.class);

        register(CreateCommand.class);

    }

    protected CommandRunner(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public String execute(CommandCallable command) throws Exception {
        Future<String> future = executorService.submit(command);
        while (!future.isDone()) {
            Thread.sleep(100l);
        };

        return future.get();
    }

    public static void main(String[] args) {

        try {
            String result = CommandRunner.instance.execute(create(args));

            System.out.println(result);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static CommandCallable create(String[] args) throws Exception {
        String cmd = action(args);
        if(classMap.containsKey(cmd)) {
            Class<? extends CommandCallable> cls = classMap.get(cmd);
            Options options = optionsMap.get(cmd);

            CommandCallable command = cls.newInstance();
            CommandLine commandLine = new DefaultParser().parse(options, args);

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

        return new HelpCommand();
    }

    private static String action(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-a".equals(args[i])) {
                String cmd = args[i + 1];
                if (cmd.startsWith("-")) {
                    throw new IllegalArgumentException("");
                }

                return cmd;
            }
        }

        throw new IllegalArgumentException("");
    }

    private static void register(Class<? extends CommandCallable> cls) {
        String name = cls.getAnnotation(Command.class).name();
        classMap.put(name, cls);

        Options options = new Options();
        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .required(true)
                .desc("")
                .build());

        Class superClass = cls;
        while(!Object.class.equals(superClass)) {
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

    static class HelpCommand implements CommandCallable {

        @Override
        public String call() throws Exception {
            return "Help";
        }
    }
}
