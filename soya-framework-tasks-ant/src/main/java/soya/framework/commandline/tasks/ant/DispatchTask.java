package soya.framework.commandline.tasks.ant;

import soya.framework.commandline.Command;

@Command(group = "apache-ant", name = "dispatch", httpMethod = Command.HttpMethod.POST)
public class DispatchTask extends AntTask<Dispatch> {

    @Override
    protected void prepare(Dispatch task) throws Exception {

    }
}
