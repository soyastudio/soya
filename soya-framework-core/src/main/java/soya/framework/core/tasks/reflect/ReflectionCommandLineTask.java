package soya.framework.core.tasks.reflect;

import soya.framework.core.Command;
import soya.framework.core.TaskExecutionContext;
import soya.framework.core.TaskName;
import soya.framework.core.tasks.CommandLineTask;

import java.net.URI;
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
