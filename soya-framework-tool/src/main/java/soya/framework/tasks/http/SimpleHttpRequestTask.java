package soya.framework.tasks.http;

import soya.framework.core.Command;
import soya.framework.core.Task;
import soya.framework.core.CommandOption;

@Command(group = "http-client", name = "simple")
public class SimpleHttpRequestTask extends Task<String> {

    @CommandOption(option = "u", required = true)
    private String url;

    @CommandOption(option = "b", dataForProcessing = true)
    private String body;

    @Override
    public String execute() throws Exception {

        
        return null;
    }
}
