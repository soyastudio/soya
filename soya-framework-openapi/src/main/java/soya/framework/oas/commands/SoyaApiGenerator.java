package soya.framework.oas.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandExecutionContext;
import soya.framework.oas.swagger.SwaggerBuilder;

@Command(group = "reflect", name = "swagger",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SoyaApiGenerator implements CommandCallable<String> {

    @Override
    public String call() throws Exception {
        return SwaggerBuilder.create(CommandExecutionContext.getInstance()).toJson();
    }

}
