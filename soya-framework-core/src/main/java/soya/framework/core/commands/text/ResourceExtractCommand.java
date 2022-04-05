package soya.framework.core.commands.text;

import soya.framework.core.Command;
import soya.framework.core.commands.ResourceCommand;

@Command(group = "text-util", name = "extract")
public class ResourceExtractCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        return contents();
    }
}
