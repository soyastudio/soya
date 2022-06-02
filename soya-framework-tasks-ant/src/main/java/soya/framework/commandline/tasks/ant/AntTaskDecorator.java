package soya.framework.commandline.tasks.ant;

import soya.framework.commandline.Command;
import soya.framework.commandline.TaskCallable;
import soya.framework.commandline.TaskResult;

@Command(group = "apache-ant", name = "task", httpMethod = Command.HttpMethod.POST)
public class AntTaskDecorator implements TaskCallable {

    @Override
    public TaskResult call() throws Exception {
        return null;
    }
}
