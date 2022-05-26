package soya.framework.commandline.tasks.reflect;

import soya.framework.commandline.Command;
import soya.framework.commandline.TaskExecutionContext;
import soya.framework.commandline.tasks.CommandLineTask;

import java.util.Properties;

@Command(group = "reflect", name = "commandline")
public class ReflectionCommandLineTask extends CommandLineTask {
    private static Properties defaultCommandMappings;

    protected void loadCommand() {
        TaskExecutionContext.getInstance().getCommands("reflect").forEach(e -> {
            if(!"commandline".equals(e.getName())) {
                aliasMap.put(e.getName(), e);
            }
        });
    }
}
