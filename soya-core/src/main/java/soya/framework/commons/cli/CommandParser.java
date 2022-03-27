package soya.framework.commons.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class CommandParser {

    private static DefaultParser parser = new DefaultParser();
    private CommandParser() {

    }

    public static Options parse(Class<? extends CommandCallable> cmd) {
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

    public static Field[] getOptionFields(Class<? extends CommandCallable> cmd) {
        Command command = cmd.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Command is not defined: " + cmd.getName());
        }

        List<Field> list = new ArrayList<>();
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
                    list.add(field);
                }
            }
            superClass = superClass.getSuperclass();
        }

        return list.toArray(new Field[list.size()]);
    }

    public static CommandCallable<?> create(Class<? extends CommandCallable> cls, String[] args) throws Exception {
        CommandCallable<?> cmd = cls.newInstance();
        CommandLine commandLine = parser.parse(parse(cls), args);

        Field[] fields = getOptionFields(cls);
        for(Field field: fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            String value = commandLine.getOptionValue(commandOption.option());
            if(value != null && !"null".equals(value)) {
                field.setAccessible(true);
                field.set(cmd, value);
            }
        }

        return cmd;
    }

}
