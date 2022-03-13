package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.CommandCallable;
import soya.framework.commons.cli.CommandOption;
import soya.framework.commons.cli.Resources;

public abstract class ResourceCommand implements CommandCallable<String> {

    @CommandOption(option = "s", longOption = "source", required = true)
    protected String source;

    protected String contents() throws Exception {
        return Resources.get(source);
    }
}
