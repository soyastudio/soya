package soya.framework.core.tasks.reflect;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.core.Resource;

@Command(group = "reflect", name = "resource", httpMethod = Command.HttpMethod.GET)
public class ResourceExtractionTask extends ReflectionTask<String> {

    @CommandOption(option = "u", required = true)
    protected String uri;

    @Override
    protected String execute() throws Exception {
        return Resource.create(uri).getAsString();
    }
}
