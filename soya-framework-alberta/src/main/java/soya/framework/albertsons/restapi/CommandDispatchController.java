package soya.framework.albertsons.restapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.text.StrSubstitutor;
import soya.framework.commons.cli.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Path("/cmd")
@Api(value = "Command Service")
public class CommandDispatchController extends CommandDispatcher {

    @GET
    @Path("/groups")
    @Produces({MediaType.APPLICATION_JSON})
    public Response groups() {
        Set<String> set = new HashSet<>();
        List<String> uris = CommandExecutionContext.getInstance().getCommands();
        uris.forEach(e -> {
            try {
                set.add(new URI(e).getScheme());

            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        });

        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return Response.ok(list).build();
    }

    @GET
    @Path("/commands")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Response commands(@QueryParam("group") String group) {
        if (group == null) {
            return Response.ok(CommandExecutionContext.getInstance().getCommands()).build();
        } else {
            return Response.ok(CommandExecutionContext.getInstance().getCommands(group)).build();
        }
    }

    @GET
    @Path("/command/{group}/{command}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Response command(@PathParam("group") String group, @PathParam("command") String command) {
        String uri = group + "://" + command;
        Class<? extends CommandCallable> cls = CommandExecutionContext.getInstance().getCommandType(uri);
        Command cmd = cls.getAnnotation(Command.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group", cmd.group());
        jsonObject.addProperty("name", cmd.name());
        jsonObject.addProperty("description", cmd.desc());

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

        return Response.ok(jsonObject).build();

    }

    @POST
    @Path("/uri")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response uri(String uri) throws Exception {
        String token = StrSubstitutor.replace(uri, CommandExecutionContext.getInstance().getProperties());
        token = StrSubstitutor.replace(token, System.getProperties());
        URI command = new URI(token);
        return Response.ok(_dispatch(command)).build();

    }

    @POST
    @Path("/execute/{group}/{command}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response execute(@PathParam("group") String group, @PathParam("command") String command, @HeaderParam("arguments") String arguments, String message) throws Exception {
        String uri = group + "://" + command;
        return Response.ok(_dispatch(uri, arguments, message)).build();

    }


}
