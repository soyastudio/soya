package soya.framework.dispatch.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandExecutionContext;

@Command(group = "dispatch", name = "groups", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class GroupsCommand extends DispatchCommand<String> {

    @Override
    public String call() throws Exception {
        return toJson(CommandExecutionContext.getInstance().groups());
    }
}
