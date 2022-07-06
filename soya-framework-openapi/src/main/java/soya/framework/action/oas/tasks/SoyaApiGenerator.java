package soya.framework.action.oas.tasks;

import soya.framework.action.Command;
import soya.framework.action.Action;
import soya.framework.action.ActionContext;
import soya.framework.action.oas.swagger.SwaggerBuilder;

@Command(group = "reflect", name = "swagger",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SoyaApiGenerator extends Action<String> {

    @Override
    public String execute() throws Exception {
        return SwaggerBuilder.create(ActionContext.getInstance()).toJson();
    }

}
