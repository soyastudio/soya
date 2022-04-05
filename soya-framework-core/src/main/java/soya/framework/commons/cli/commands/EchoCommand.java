package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

@Command(group = "resource", name = "echo", desc = "TODO")
public class EchoCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        return getResourceAsString();
    }
}
