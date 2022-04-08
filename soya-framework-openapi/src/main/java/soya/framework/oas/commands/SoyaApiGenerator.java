package soya.framework.oas.commands;

import soya.framework.core.*;
import soya.framework.oas.swagger.Swagger;

import java.lang.reflect.Field;
import java.util.List;

@Command(group = "reflect", name = "swagger",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SoyaApiGenerator implements CommandCallable<String> {

    @Override
    public String call() throws Exception {
        CommandExecutionContext context = CommandExecutionContext.getInstance();
        List<String> groups = context.groups();
        Swagger.SwaggerBuilder builder = Swagger.builder();

        builder.addSchema("http").addSchema("https")
                .contactName("Qun Wen")
                .contactEmail("wenqun.soya@gmail.com");

        builder.addConsume("text/plain", "application/json", "application/xml");
        builder.addProduce("text/plain", "application/json", "application/xml");

        groups.forEach(e -> {
            builder.addTag(e, e);
        });

        for(String group: groups) {
            context.getCommands(group).forEach(e -> {
                buildPath(context.getCommandType(e), builder);
            });
        }

        return builder.build().toJson();
    }

    private void buildPath(Class<? extends CommandCallable> cls, Swagger.SwaggerBuilder builder) {

        Command command = cls.getAnnotation(Command.class);
        Field[] fields = CommandParser.getOptionFields(cls);
        String path = "/" + command.group() + "/" + command.name();

        for(Field field: fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            if(commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                path = path + "/{" + field.getName() + "}";
            }
        }

        Swagger.PathBuilder pathBuilder = builder.pathBuilder(path , command.httpMethod().name(), command.name());

        for(Field field: fields) {
            CommandOption commandOption = field.getAnnotation(CommandOption.class);
            if(!commandOption.paramType().equals(CommandOption.ParamType.ReferenceParam)) {

            }
        }

        pathBuilder.build();
        //builder.addPath(pathBuilder);
    }


}
