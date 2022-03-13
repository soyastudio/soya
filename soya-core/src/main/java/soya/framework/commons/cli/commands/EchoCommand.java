package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

@Command(name = "echo", uri = "resource://echo")
public class EchoCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        String msg = contents();
        System.out.println(msg);
        return msg;
    }
}
