package soya.framework.tasks.http;

import soya.framework.commandline.Command;
import soya.framework.commandline.Task;
import soya.framework.commandline.CommandOption;

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
