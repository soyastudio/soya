package soya.framework.oas.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;

@Command(group = "http-client", name = "rest-call",
        httpMethod = Command.HttpMethod.POST)
public class ApiInvocationCommand implements CommandCallable<String> {

    @CommandOption(option = "a", required = true)
    protected String api;

    @CommandOption(option = "p", required = true)
    protected String path;

    @CommandOption(option = "m", required = true)
    protected String method;

    @CommandOption(option = "c")
    protected String commandline;

    @CommandOption(option = "b", dataForProcessing = true)
    protected String body;

    @Override
    public String call() throws Exception {

        return null;
    }
}
