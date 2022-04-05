package soya.framework.core.commands;

import soya.framework.core.Command;

@Command(group = "test", name = "echo", desc = "TODO")
public class EchoCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        return getResourceAsString();
    }
}
