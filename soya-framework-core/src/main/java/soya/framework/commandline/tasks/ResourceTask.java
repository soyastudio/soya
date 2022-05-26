package soya.framework.commandline.tasks;

import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Task;

public abstract class ResourceTask<T> extends Task<T> {

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
