package soya.framework.commands.apache.ant;

import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.io.File;

@Command(group = "apache-ant", name = "mkdir", httpMethod = Command.HttpMethod.POST)
public class MkdirCommand extends AntTaskCommand<Mkdir> {

    @CommandOption(option = "d", required = true)
    protected String dir;

    @Override
    protected void init() {
        File directory = new File(home(), dir);
        task.setDir(directory);
    }
}
