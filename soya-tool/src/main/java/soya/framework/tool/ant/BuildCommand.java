package soya.framework.tool.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;

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
