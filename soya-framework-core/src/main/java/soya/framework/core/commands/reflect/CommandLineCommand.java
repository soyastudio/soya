package soya.framework.core.commands.reflect;

import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import soya.framework.core.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

@Command(group = "reflect", name = "commandline")
public class CommandLineCommand extends ReflectCommand<String> {
    private static Properties defaultCommandMappings;

    static {
        defaultCommandMappings = new Properties();
        defaultCommandMappings.put("groups", "reflect://groups");
        defaultCommandMappings.put("cmds", "reflect://commands");
        defaultCommandMappings.put("cmd", "reflect://command");

    }

    @CommandOption(option = "c", required = true)
    protected String commandline;

    @CommandOption(option = "p", dataForProcessing = true)
    protected String payload;

    protected Properties commandMappings = new Properties();

    public CommandLineCommand() {
        loadCommand();
    }

    @Override
    public String call() throws Exception {
        List<String> list = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(commandline);
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }

        String uri = list.get(0);
        if (!uri.contains("://")) {
            uri = commandMappings.getProperty(uri);

        }
        List<String> args = list.subList(1, list.size());

        Class<? extends CommandCallable> cls = CommandExecutionContext.getInstance().getCommandType(uri);
        Field dataInputField = CommandParser.getDataInputField(cls);
        if(dataInputField != null) {
            CommandOption commandOption = dataInputField.getAnnotation(CommandOption.class);
            args.add("-" + commandOption.option());
            args.add("DATA-INPUT");
        }

        Options options = CommandParser.parse(cls);
        CommandLine cmd = new DefaultParser().parse(options, args.toArray(new String[args.size()]));

        Field[] fields = CommandParser.getOptionFields(cls);


        CommandCallable callable = cls.newInstance();

        for (Field field : fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            String value = null;
            if (CommandOption.ParamType.ReferenceParam.equals(commandOption.paramType())) {
                value = CommandExecutionContext.getInstance().getProperty(commandOption.referenceKey());

            } else if(commandOption.dataForProcessing()) {
                value = payload;

            }else {
                value = cmd.getOptionValue(commandOption.option());

            }

            if (value != null) {
                field.setAccessible(true);
                field.set(callable, value);
            }
        }

        Future<?> future = CommandExecutionContext.getInstance().getExecutorService().submit(callable);
        while (future.isDone()) {
            Thread.sleep(100l);
        }

        Object result = future.get();
        if (result == null) {
            return null;

        } else if (result instanceof String) {
            return (String) result;

        } else {
            return new GsonBuilder().setPrettyPrinting().create().toJson(result);

        }
    }


    protected void loadCommand() {
        commandMappings.putAll(defaultCommandMappings);
    }
}
