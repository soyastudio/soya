package soya.framework.action.actions;

import soya.framework.action.Action;
import soya.framework.action.CommandOption;

public abstract class ResourceAction<T> extends Action<T> {

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
