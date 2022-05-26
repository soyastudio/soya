package soya.framework.commandline.tasks.reflect;

import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Resource;

@Command(group = "reflect", name = "resource", httpMethod = Command.HttpMethod.GET)
public class ResourceExtractionTask extends ReflectionTask<String> {

    @CommandOption(option = "u", required = true)
    protected String uri;

    @Override
    protected String execute() throws Exception {
        return Resource.create(uri).getAsString();
    }
}
