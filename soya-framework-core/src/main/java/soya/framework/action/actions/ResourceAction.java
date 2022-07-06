package soya.framework.action.actions;

import soya.framework.action.CommandOption;
import soya.framework.action.Action;

public abstract class ResourceAction<T> extends Action<T> {

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
