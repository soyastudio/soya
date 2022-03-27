package soya.framework.tool.ant;

import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandOption;

import java.io.File;

@Command(group = "ant", name = "mkdir")
public class MkdirCommand extends AntTaskCommand<Mkdir> {

    @CommandOption(option = "d", longOption = "dir", required = true)
    protected String dir;

    @Override
    protected void init() {
        File directory = new File(dir);
        task.setDir(directory);
    }
}
