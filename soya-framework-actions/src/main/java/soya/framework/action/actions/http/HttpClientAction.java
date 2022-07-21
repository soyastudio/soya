package soya.framework.action.actions.http;

import soya.framework.action.Action;
import soya.framework.action.CommandOption;

public abstract class HttpClientAction<T> extends Action<T> {

    @CommandOption(option = "u", required = true)
    protected String url;
}
