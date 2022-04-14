package soya.framework.commands.http;

import soya.framework.core.Command;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;

@Command(group = "http-client", name = "simple")
public class SimpleHttpRequestCommand implements CommandCallable<String> {

    @CommandOption(option = "u", required = true)
    private String url;

    @CommandOption(option = "b", dataForProcessing = true)
    private String body;

    @Override
    public String call() throws Exception {

        
        return null;
    }
}
