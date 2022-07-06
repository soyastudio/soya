package soya.framework.action.actions.http;

import soya.framework.action.Command;
import soya.framework.action.Action;
import soya.framework.action.CommandOption;

@Command(group = "http-client", name = "simple")
public class SimpleHttpRequestAction extends Action<String> {

    @CommandOption(option = "u", required = true)
    private String url;

    @CommandOption(option = "b", dataForProcessing = true)
    private String body;

    @Override
    public String execute() throws Exception {

        
        return null;
    }
}
