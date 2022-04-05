package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

@Command(group = "resource", name = "extract")
public class ResourceExtractCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        return contents();
    }
}
