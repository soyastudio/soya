package soya.framework.tasks.jgit;

import soya.framework.commandline.Command;
import soya.framework.commandline.TaskResult;

@Command(group = "git", name = "init", httpMethod = Command.HttpMethod.POST)
public class InitTask extends GitTask {

    @Override
    public TaskResult call() throws Exception {

        return null;
    }
}
