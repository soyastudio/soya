package soya.framework.action.actions.text;

import soya.framework.action.Action;
import soya.framework.action.Domain;
import soya.framework.action.CommandOption;

import java.nio.charset.Charset;

@Domain(group = "text-util",
        title = "Text Processing Commands",
        description = "Commands for processing text.")
public abstract class TextUtilAction extends Action<String> {

    @CommandOption(option = "c", defaultValue = "utf-8")
    protected String encoding = Charset.defaultCharset().toString();

    @CommandOption(option = "s", required = true, dataForProcessing = true)
    protected String source;

}
