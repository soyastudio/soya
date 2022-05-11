package soya.framework.core.tasks.text;

import soya.framework.core.CommandGroup;
import soya.framework.core.CommandOption;
import soya.framework.core.Task;

import java.nio.charset.Charset;

@CommandGroup(group = "text-util",
        title = "Text Processing Tool",
        description = "Toolkit for processing text.")
public abstract class TextUtilTask extends Task<String> {

    @CommandOption(option = "c")
    protected String encoding = Charset.defaultCharset().toString();

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
