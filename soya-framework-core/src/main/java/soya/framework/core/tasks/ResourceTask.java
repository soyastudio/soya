package soya.framework.core.tasks;

import soya.framework.core.CommandOption;
import soya.framework.core.Task;

public abstract class ResourceTask<T> extends Task<T> {

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
