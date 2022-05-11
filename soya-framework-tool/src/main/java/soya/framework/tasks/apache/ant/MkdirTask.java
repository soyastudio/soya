package soya.framework.tasks.apache.ant;

import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.io.File;

@Command(group = "apache-ant", name = "mkdir", httpMethod = Command.HttpMethod.POST)
public class MkdirTask extends AntTask<Mkdir> {

    @CommandOption(option = "d", required = true)
    protected String dir;

    @Override
    protected void prepare(Mkdir task) throws Exception {
        File directory = new File(antHome, dir);
        task.setDir(directory);
    }
}
