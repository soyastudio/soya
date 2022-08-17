package soya.framework.action.oas.swagger;

import soya.framework.action.*;

import java.lang.reflect.Field;
import java.util.List;

public class SwaggerBuilder {

    public static Swagger create(ActionContext context) {
        List<String> groups = context.groups();
        Swagger.SwaggerBuilder builder = Swagger.builder();

        builder.addSchema("HTTP").addSchema("HTTPS")
                .contactName("Qun Wen")
                .contactEmail("wenqun.soya@gmail.com");

        builder.addConsume("text/plain", "application/json", "application/xml");
        builder.addProduce("text/plain", "application/json", "application/xml");

        groups.forEach(e -> {
            if (context.groupDescription(e) != null) {
                ActionContext.GroupDescription groupDescription = context.groupDescription(e);
                Swagger.TagObject tagObject = Swagger.TagObject.instance();
                builder.addTag(Swagger.TagObject.instance()
                        .name(groupDescription.getTitle())
                        .description(groupDescription.getDescription())
                );

            } else {
                builder.addTag(Swagger.TagObject.instance().name(e));
            }
        });

        for (String group : groups) {
            context.getCommands(group).forEach(e -> {
                buildPath(e, builder);
            });
        }

        return builder.build();
    }

    private static void buildPath(ActionName actionName, Swagger.SwaggerBuilder builder) {
        ActionClass actionClass = ActionClass.get(actionName);
        Class<? extends ActionCallable> cls = actionClass.getActionType();

        Command command = cls.getAnnotation(Command.class);
        ActionContext.GroupDescription groupDescription = ActionContext.getInstance().groupDescription(command.group());
        String tag = groupDescription == null ? command.group() : groupDescription.getTitle();

        Field[] fields = actionClass.getActionFields();
        String path = "/" + command.group() + "/" + command.name();

        for (Field field : fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            if (commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                path = path + "/{" + field.getName() + "}";
            }
        }

        Swagger.PathBuilder pathBuilder = builder.pathBuilder(path, command.httpMethod().name(), command.name());
        pathBuilder.addTag(tag);

        for (Field field : fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            CommandOption.ParamType paramType = commandOption.paramType();
            if (paramType.equals(CommandOption.ParamType.ReferenceParam)) {

            } else if (commandOption.dataForProcessing()) {
                Swagger.BodyParameterBuilder bodyParameterBuilder = pathBuilder.bodyParameterBuilder("body", commandOption.desc());
                if (commandOption.required()) {
                    bodyParameterBuilder.required();
                }

                bodyParameterBuilder.build();

            } else {
                String param = paramType.name().substring(0, paramType.name().indexOf("Param")).toLowerCase();
                Swagger.SimpleParameterBuilder parameterBuilder = pathBuilder.parameterBuilder(field.getName(), param, commandOption.desc());
                if (commandOption.required()) {
                    parameterBuilder.required();
                }

                parameterBuilder.build();
            }
        }

        for (Command.MediaType t : command.httpRequestTypes()) {
            if (Command.MediaType.APPLICATION_JSON.equals(t)) {
                pathBuilder.consumes("application/json");

            } else if (Command.MediaType.APPLICATION_XML.equals(t)) {
                pathBuilder.consumes("application/xml");

            } else {
                pathBuilder.consumes("text/plain");
            }
        }

        for (Command.MediaType t : command.httpResponseTypes()) {

            if (Command.MediaType.APPLICATION_JSON.equals(t)) {
                pathBuilder.produces("application/json");

            } else if (Command.MediaType.APPLICATION_XML.equals(t)) {
                pathBuilder.produces("application/xml");

            } else if (Command.MediaType.TEXT_PLAIN.equals(t)){
                pathBuilder.produces("text/plain");

            } else if (Command.MediaType.APPLICATION_OCTET_STREAM.equals(t)) {
                pathBuilder.produces("application/octet-stream");

            }
        }

        pathBuilder.build();
    }
}
