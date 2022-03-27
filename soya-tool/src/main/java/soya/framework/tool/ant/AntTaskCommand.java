package soya.framework.tool.ant;

import org.apache.tools.ant.Task;
import soya.framework.commons.cli.CommandCallable;

public abstract class AntTaskCommand<T extends Task> extends AntTask<T> implements CommandCallable<Void> {

    @Override
    public Void call() throws Exception {
        init();

        return invoke();
    }
}
