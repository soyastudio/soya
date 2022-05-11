package soya.framework.core.oas.tasks;

import soya.framework.core.Command;
import soya.framework.core.Task;
import soya.framework.core.TaskExecutionContext;
import soya.framework.core.oas.swagger.SwaggerBuilder;

@Command(group = "reflect", name = "swagger",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SoyaApiGenerator extends Task<String> {

    @Override
    public String execute() throws Exception {
        return SwaggerBuilder.create(TaskExecutionContext.getInstance()).toJson();
    }

}
