package soya.framework.core.commands.reflect;

import soya.framework.core.Command;
import soya.framework.core.CommandExecutionContext;
import soya.framework.core.CommandOption;

import java.util.List;

@Command(group = "reflect", name = "commands", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class CommandsCommand extends ReflectCommand<String> {

    @CommandOption(option = "g")
    private String group;

    @Override
    public String call() throws Exception {
        List<String> list;
        if (group == null) {
            list = CommandExecutionContext.getInstance().getCommands();
        } else {
            list = CommandExecutionContext.getInstance().getCommands(group);
        }

        return toJson(list);
    }
}
