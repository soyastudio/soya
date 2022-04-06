package soya.framework.commands.ant;

import org.apache.tools.ant.taskdefs.Ant;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "ant", name = "build")
public class BuildCommand extends AntTaskCommand<Ant> {

    @CommandOption(option = "b", longOption = "base", required = true)
    private String baseDir;

    @CommandOption(option = "f", longOption = "buildFile", required = true)
    private String buildFile;

    @CommandOption(option = "t", longOption = "task")
    private String task;

    @Override
    protected void init() {

    }
}
