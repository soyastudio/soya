package soya.framework.action.actions.reflect;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.cli.Options;
import soya.framework.action.*;

import java.lang.reflect.Field;
import java.net.URI;

@Command(group = "reflect", name = "overview", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class OverviewAction extends ReflectionAction<JsonElement> {
    private ActionContext context = ActionContext.getInstance();

    @CommandOption(option = "q")
    private String filter;

    @Override
    public JsonElement execute() throws Exception {
        if (filter == null || filter.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            context.groups().forEach(group -> {
                jsonObject.add(group, groupInfo(group));
            });

            return jsonObject;

        } else if (filter.contains("://")) {
            URI uri = new URI(filter);
            String key = uri.getScheme() + "://" + uri.getHost();
            Class<? extends ActionCallable> cls = context.getActionType(ActionName.fromURI(key));

            return commandInfo(cls);

        } else {
            return groupInfo(filter);
        }
    }

    private JsonArray groupInfo(String group) {
        JsonArray array = new JsonArray();
        context.getCommands(group).forEach(taskName -> {
            Class<? extends ActionCallable> cls = context.getActionType(taskName);
            if (cls.getAnnotation(Command.class) != null) {
                array.add(commandInfo(cls));
            }

        });

        return array;
    }

    private JsonObject commandInfo(Class<? extends ActionCallable> cls) {
        Gson gson = new Gson();
        Command cmd = cls.getAnnotation(Command.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", cmd.name());
        jsonObject.addProperty("id", cmd.group() + "://" + cmd.name());
        jsonObject.addProperty("type", cls.getName());
        jsonObject.add("description", gson.toJsonTree(cmd.description()));

        JsonArray array = new JsonArray();
        Field[] fields = ActionParser.getOptionFields(cls);
        Options options = ActionParser.parse(cls);

        StringBuilder builder = new StringBuilder();
        StringBuilder uriBuilder = new StringBuilder(cmd.group()).append("://").append(cmd.name());
        if (fields.length > 0) {
            uriBuilder.append("?");
        }

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            if (commandOption != null) {
                JsonObject option = new JsonObject();
                option.addProperty("option", commandOption.option());
                option.addProperty("longOption", options.getOption(commandOption.option()).getLongOpt());
                if (commandOption.dataForProcessing()) {
                    option.addProperty("dataForProcessing", true);

                }
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
                        builder.append(" {").append(options.getOption(commandOption.option()).getLongOpt()).append("}");
                        uriBuilder.append("{").append(options.getOption(commandOption.option()).getLongOpt()).append("}");
                    }
                }
            }
        }

        jsonObject.add("options", array);

        jsonObject.addProperty("command-line", builder.toString().trim());
        jsonObject.addProperty("uri", uriBuilder.toString().trim());

        return jsonObject;
    }
}
