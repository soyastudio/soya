package soya.framework.core.commands.reflect;

import soya.framework.core.Command;
import soya.framework.core.CommandExecutionContext;

@Command(group = "reflect", name = "groups", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class GroupsCommand extends ReflectCommand<String> {

    @Override
    public String call() throws Exception {
        return toJson(CommandExecutionContext.getInstance().groups());
    }
}
