package soya.framework.commandline.tasks.text;

import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Task;

import java.nio.charset.Charset;

@CommandGroup(group = "text-util",
        title = "Text Processing Commands",
        description = "Commands for processing text.")
public abstract class TextUtilTask extends Task<String> {

    @CommandOption(option = "c")
    protected String encoding = Charset.defaultCharset().toString();

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
