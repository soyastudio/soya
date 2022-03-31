package soya.framework.commons.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        Collections.sort(list, new CommandFieldComparator());

        return list.toArray(new Field[list.size()]);
    }

    public static CommandCallable<?> create(Class<? extends CommandCallable> cls, String[] args) throws Exception {
        CommandCallable<?> cmd = cls.newInstance();
        CommandLine commandLine = parser.parse(parse(cls), args);

        Field[] fields = getOptionFields(cls);
        for (Field field : fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            String value = commandLine.getOptionValue(commandOption.option());
            if (value != null && !"null".equals(value)) {
                field.setAccessible(true);
                field.set(cmd, value);
            }
        }

        return cmd;
    }

    static class CommandFieldComparator implements Comparator<Field> {
        @Override
        public int compare(Field o1, Field o2) {
            CommandOption commandOption1 = o1.getAnnotation(CommandOption.class);
            CommandOption commandOption2 = o2.getAnnotation(CommandOption.class);

            Class<?> cls1 = o1.getDeclaringClass();
            Class<?> cls2 = o2.getDeclaringClass();

            if (commandOption1.dataForProcessing() && !commandOption2.dataForProcessing()) {
                return 1;

            } else if (!commandOption1.dataForProcessing() && commandOption2.dataForProcessing()) {
                return -1;

            } else {
                int paramDiff = CommandOption.ParamType.indexOf(commandOption1.paramType()) - CommandOption.ParamType.indexOf(commandOption2.paramType());
                if (paramDiff != 0) {
                    return paramDiff;

                } else if (cls1.equals(cls2)) {
                    return o1.getName().compareTo(o2.getName());

                } else if (cls2.isAssignableFrom(cls1)) {
                    return 1;

                } else {
                    return -1;
                }

            }
        }
    }

}
