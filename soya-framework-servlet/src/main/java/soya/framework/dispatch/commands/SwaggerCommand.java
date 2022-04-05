package soya.framework.dispatch.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandExecutionContext;
import soya.framework.commons.cli.CommandOption;
import soya.framework.dispatch.swagger.Swagger;
import soya.framework.dispatch.swagger.SwaggerBuilder;

@Command(group = "dispatch", name = "swagger", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SwaggerCommand extends DispatchCommand<String> {

    @CommandOption(option = "b", longOption = "basePath", paramType = CommandOption.ParamType.QueryParam)
    private String basePath = "api";

    @Override
    public String call() throws Exception {
        Swagger swagger = SwaggerBuilder.create(CommandExecutionContext.getInstance(), basePath);
        return toJson(swagger);
    }
}
