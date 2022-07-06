package soya.framework.action.actions.reflect;

import soya.framework.action.Command;
import soya.framework.action.ActionContext;
import soya.framework.action.actions.CommandLineAction;

import java.util.Properties;

@Command(group = "reflect", name = "commandline")
public class ReflectionCommandLineAction extends CommandLineAction {
    private static Properties defaultCommandMappings;

    protected void loadCommand() {
        ActionContext.getInstance().getCommands("reflect").forEach(e -> {
            if(!"commandline".equals(e.getName())) {
                aliasMap.put(e.getName(), e);
            }
        });
    }
}
