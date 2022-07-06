package soya.framework.action.actions.reflect;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.Resource;

@Command(group = "reflect", name = "resource", httpMethod = Command.HttpMethod.GET)
public class ResourceExtractionAction extends ReflectionAction<String> {

    @CommandOption(option = "u", required = true)
    protected String uri;

    @Override
    protected String execute() throws Exception {
        return Resource.create(uri).getAsString();
    }
}
