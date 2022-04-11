package soya.framework.commands.apache.ant;

import org.apache.tools.ant.Task;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandGroup;
import soya.framework.core.CommandOption;

import java.io.File;

@CommandGroup(group = "apache-ant", title = "Apache Ant Tool", description = "Toolkit for executing apache ant tasks and script.")
public abstract class AntTaskCommand<T extends Task> extends AntTask<T> implements CommandCallable<Void> {

    @CommandOption(option = "h", required = true, paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "ant.work.home")
    protected String home;

    @Override
    public Void call() throws Exception {
        init();

        return invoke();
    }

    protected File home() {
        File homeDir = new File(home);
        if(!homeDir.exists()) {
            homeDir.mkdirs();
        }
        return homeDir;
    }
}
