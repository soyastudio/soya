package soya.framework.dispatch.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandExecutionContext;
import soya.framework.core.CommandOption;
import soya.framework.core.commands.reflect.ReflectCommand;
import soya.framework.dispatch.swagger.Swagger;
import soya.framework.dispatch.swagger.SwaggerBuilder;

@Command(group = "swagger", name = "soya-api", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SwaggerCommand extends ReflectCommand<String> {

    @CommandOption(option = "b", paramType = CommandOption.ParamType.QueryParam)
    private String basePath = "api";

    @Override
    public String call() throws Exception {
        Swagger swagger = SwaggerBuilder.create(CommandExecutionContext.getInstance(), basePath);
        return toJson(swagger);
    }
}
