package soya.framework.commandline;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

public final class TaskParser {

    private static DefaultParser parser = new DefaultParser();

    private TaskParser() {

    }

    public static TaskName getTaskName(Class<? extends TaskCallable> cls) {
        return TaskName.fromTaskClass(cls);
    }

    public static Field[] getOptionFields(Class<? extends TaskCallable> cmd) {
        Command command = cmd.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Command is not defined: " + cmd.getName());
        }

        List<Field> list = new ArrayList<>();
        Class superClass = cmd;
        while (!Object.class.equals(superClass)) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (commandOption != null) {
                    list.add(field);
                }
            }
            superClass = superClass.getSuperclass();
        }
        Collections.sort(list, new CommandFieldComparator());

        return list.toArray(new Field[list.size()]);
    }

    public static Options parse(Class<? extends TaskCallable> cmd) {
        Command command = cmd.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Command is not defined: " + cmd.getName());
        }

        Options options = new Options();

        Class superClass = cmd;
        while (!Object.class.equals(superClass)) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                String longOption = longOption(field.getName());
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (commandOption != null && options.getOption(commandOption.option()) == null) {
                    options.addOption(Option.builder(commandOption.option())
                            .longOpt(longOption)
                            .hasArg(commandOption.hasArg())
                            .required(!CommandOption.ParamType.ReferenceParam.equals(commandOption.paramType()) && commandOption.required())
                            .desc(commandOption.desc())
                            .build());
                }
            }
            superClass = superClass.getSuperclass();
        }

        return options;
    }

    private static String longOption(String fieldName) {
        return fieldName;
    }

    public static Field getDataInputField(Class<? extends TaskCallable> cmd) {
        Field field = null;
        Field[] fields = getOptionFields(cmd);
        for (Field f : fields) {
            CommandOption commandOption = f.getAnnotation(CommandOption.class);
            if (commandOption.dataForProcessing()) {
                if (field == null) {
                    field = f;
                } else {
                    throw new IllegalArgumentException("Command has multiple data input field: " + cmd.getName());
                }
            }
        }
        return field;
    }

    public static TaskCallable fromURI(String uri) throws Exception {
        return fromURI(new URI(uri));
    }

    public static TaskCallable fromURI(URI uri) throws Exception {
        return fromURI(uri, null);

    }

    public static TaskCallable fromURI(URI uri, String payload) throws Exception {
        Class<? extends TaskCallable> cls = null;
        if ("class".equalsIgnoreCase(uri.getScheme())) {
            cls = (Class<? extends TaskCallable>) Class.forName(uri.getHost());

        } else {
            cls = TaskExecutionContext.getInstance().getTaskType(TaskName.fromURI(uri));
        }

        if (cls == null) {
            throw new IllegalArgumentException("Can not find task type from uri: " + uri);
        }

        String queryString = uri.getQuery();
        Hashtable<String, String[]> parameters = parseQueryString(uri.getQuery());

        TaskCallable task = cls.newInstance();
        Field[] fields = TaskParser.getOptionFields(cls);
        for (Field field : fields) {
            Object value = value(field, parameters, payload);
            if (value != null) {
                field.setAccessible(true);
                field.set(task, value);
            }
        }

        return task;
    }

    public static TaskCallable fromCommandLine(String commandline) throws Exception {
        return fromCommandLine(commandline, null);
    }

    public static TaskCallable fromCommandLine(String commandline, String payload) throws Exception {

        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(commandline);
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }

        String uri = list.get(0);
        List<String> args = list.subList(1, list.size());

        Class<? extends TaskCallable> cls = TaskExecutionContext.getInstance().getTaskType(TaskName.fromURI(uri));
        Field dataInputField = TaskParser.getDataInputField(cls);
        if (dataInputField != null) {
            CommandOption commandOption = dataInputField.getAnnotation(CommandOption.class);
            args.add("-" + commandOption.option());
            args.add("DATA-INPUT");
        }

        Options options = TaskParser.parse(cls);
        CommandLine cmd = new DefaultParser().parse(options, args.toArray(new String[args.size()]));

        Field[] fields = TaskParser.getOptionFields(cls);

        TaskCallable callable = cls.newInstance();
        for (Field field : fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            String value = null;
            if (CommandOption.ParamType.ReferenceParam.equals(commandOption.paramType())) {
                value = TaskExecutionContext.getInstance().getProperty(commandOption.referenceKey());

            } else if (commandOption.dataForProcessing()) {
                if (payload != null) {
                    value = payload;

                } else if (cmd.getOptionValue(commandOption.option()) != null) {
                    value = Resource.create(cmd.getOptionValue(commandOption.option())).getAsString();
                }

            } else {
                value = cmd.getOptionValue(commandOption.option());

            }

            if (value != null) {
                field.setAccessible(true);
                field.set(callable, value);
            }
        }

        return callable;
    }

    public static Hashtable<String, String[]> parseQueryString(String s) {
        String[] valArray = null;
        if (s == null) {
            throw new IllegalArgumentException();
        } else {
            Hashtable<String, String[]> ht = new Hashtable();
            StringBuilder sb = new StringBuilder();

            String key;
            for (StringTokenizer st = new StringTokenizer(s, "&"); st.hasMoreTokens(); ht.put(key, valArray)) {
                String pair = st.nextToken();
                int pos = pair.indexOf(61);
                if (pos == -1) {
                    throw new IllegalArgumentException();
                }

                key = parseName(pair.substring(0, pos), sb);
                String val = parseName(pair.substring(pos + 1, pair.length()), sb);
                if (!ht.containsKey(key)) {
                    valArray = new String[]{val};
                } else {
                    String[] oldVals = (String[]) ht.get(key);
                    valArray = new String[oldVals.length + 1];

                    for (int i = 0; i < oldVals.length; ++i) {
                        valArray[i] = oldVals[i];
                    }

                    valArray[oldVals.length] = val;
                }
            }

            return ht;
        }
    }

    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException var6) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException var7) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2) {
                            ++i;
                        }
                    }
                    break;
                case '+':
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    private static Object value(Field field, Hashtable<String, String[]> params, String payload) throws IOException {
        CommandOption commandOption = field.getAnnotation(CommandOption.class);
        String value = null;
        if (commandOption.dataForProcessing()) {
            if (payload != null) {
                value = payload;

            } else if (params.contains(commandOption.option())) {
                value = Resource.create(params.get(commandOption.option())[0]).getAsString();

            } else if (params.contains(field.getName())) {
                value = Resource.create(params.get(field.getName())[0]).getAsString();

            }

        } else if (commandOption.paramType().equals(CommandOption.ParamType.ReferenceParam)) {
            value = TaskExecutionContext.getInstance().getProperty(commandOption.referenceKey());

        } else if (params.containsKey(commandOption.option())) {
            value = params.get(commandOption.option())[0];

        } else if (params.containsKey(field.getName())) {
            value = params.get(field.getName())[0];

        }

        if (commandOption.required() && value == null) {
            throw new IllegalArgumentException("Required option is not set: " + commandOption.option());
        }

        if (value != null) {
            return ConvertUtils.convert(value, field.getType());
        }

        return null;
    }

    private static Object convert(String value, Class<?> type) {

        if (value != null) {
            return ConvertUtils.convert(value, type);
        }

        return null;
    }

    public static TaskCallable create(Class<? extends TaskCallable> cls, String[] args) throws Exception {
        TaskCallable cmd = cls.newInstance();
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
