package soya.framework.commandline.oas.tasks;

import soya.framework.commandline.Command;
import soya.framework.commandline.Task;
import soya.framework.commandline.TaskExecutionContext;
import soya.framework.commandline.oas.swagger.SwaggerBuilder;

@Command(group = "reflect", name = "swagger",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SoyaApiGenerator extends Task<String> {

    @Override
    public String execute() throws Exception {
        return SwaggerBuilder.create(TaskExecutionContext.getInstance()).toJson();
    }

}
