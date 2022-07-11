package soya.framework.action.actions.reflect;

import soya.framework.action.Command;

@Command(group = "reflect", name = "dispatch", httpMethod = Command.HttpMethod.POST, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class DispatchAction extends ReflectionAction<String> {

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
