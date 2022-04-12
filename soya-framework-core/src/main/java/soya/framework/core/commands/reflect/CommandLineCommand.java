package soya.framework.core.commands.reflect;

import soya.framework.core.Command;
import soya.framework.core.commands.DispatchCommand;

import java.util.Properties;

@Command(group = "reflect", name = "commandline")
public class CommandLineCommand extends DispatchCommand {
    private static Properties defaultCommandMappings;

    static {
        defaultCommandMappings = new Properties();
        defaultCommandMappings.put("overview", "reflect://overview");

    }

    protected void loadCommand() {
        commandMappings.putAll(defaultCommandMappings);
    }
}
