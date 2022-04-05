package soya.framework.core.commands.reflect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import soya.framework.core.*;

import java.lang.reflect.Field;

@Command(group = "reflect", name = "command", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class CommandCommand extends ReflectCommand<String> {
    @CommandOption(option = "g", longOption = "group", required = true)
    private String group;

    @CommandOption(option = "c", longOption = "command", required = true)
    private String command;

    @Override
    public String call() throws Exception {
        String uri = group + "://" + command;
        Class<? extends CommandCallable> cls = CommandExecutionContext.getInstance().getCommandType(uri);
        Command cmd = cls.getAnnotation(Command.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group", cmd.group());
        jsonObject.addProperty("name", cmd.name());

        JsonArray desc = new JsonArray();
        for(String ln: cmd.desc()) {
            desc.add(desc);
        }
        jsonObject.add("description", desc);

        JsonArray array = new JsonArray();

        Class<?> superClass = cls;
        Field[] fields = CommandParser.getOptionFields(cls);
        StringBuilder builder = new StringBuilder();
        StringBuilder uriBuilder = new StringBuilder(group).append("://").append(command);
        if (fields.length > 0) {
            uriBuilder.append("?");
        }

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
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

                builder.append(" -").append(commandOption.option());

                if (i > 0) {
                    uriBuilder.append("&");
                }

                uriBuilder.append(commandOption.option()).append("=");
                if (commandOption.hasArg()) {
                    if (!commandOption.referenceKey().isEmpty()) {
                        builder.append(" {").append(commandOption.referenceKey()).append("}");
                        uriBuilder.append("${").append(commandOption.referenceKey()).append("}");

                    } else if (!commandOption.defaultValue().isEmpty()) {
                        builder.append(" {").append(commandOption.defaultValue()).append("}");
                        uriBuilder.append("{").append(commandOption.defaultValue()).append("}");

                    } else {
                        builder.append(" {").append(commandOption.longOption()).append("}");
                        uriBuilder.append("{").append(commandOption.longOption()).append("}");
                    }
                }
            }
        }

        jsonObject.add("options", array);

        jsonObject.addProperty("command-line", builder.toString().trim());
        jsonObject.addProperty("uri", uriBuilder.toString().trim());

        return toJson(jsonObject);
    }
}
