package soya.framework.core.commands.reflect;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import soya.framework.core.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

@Command(group = "reflect", name = "execute")
public class DispatchCommand extends ReflectCommand<Object> {

    @CommandOption(option = "g", required = true)
    private String group;

    @CommandOption(option = "c", required = true)
    private String command;

    @CommandOption(option = "a")
    private String arguments;

    @CommandOption(option = "p", dataForProcessing = true)
    private String payload;

    @Override
    public Object call() throws Exception {
        String uri = group + "://" + command;

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

        if(payload != null) {
            list.add(encodeMessage(payload));
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

    private String encodeMessage(String message) {
        return "base64://" + Base64.getEncoder().encodeToString(message.getBytes());
    }
}
