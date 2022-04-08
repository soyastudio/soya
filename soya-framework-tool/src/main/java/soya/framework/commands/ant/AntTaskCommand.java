package soya.framework.commands.ant;

import org.apache.tools.ant.Task;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;

import java.io.File;

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
