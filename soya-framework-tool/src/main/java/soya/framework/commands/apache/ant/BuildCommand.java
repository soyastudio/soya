package soya.framework.commands.apache.ant;

import org.apache.tools.ant.taskdefs.Ant;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "apache-ant", name = "build")
public class BuildCommand extends AntTaskCommand<Ant> {

    @CommandOption(option = "b", required = true)
    private String baseDir;

    @CommandOption(option = "f", required = true)
    private String buildFile;

    @CommandOption(option = "t")
    private String task;

    @Override
    protected void init() {

    }
}
