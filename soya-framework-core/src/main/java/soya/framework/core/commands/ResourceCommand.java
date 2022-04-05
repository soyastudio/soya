package soya.framework.core.commands;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

import java.io.InputStream;

public abstract class ResourceCommand implements CommandCallable<String> {

    @CommandOption(option = "s", longOption = "source", required = true, dataForProcessing = true)
    protected String source;

    protected String contents() throws Exception {
        return Resources.getResourceAsString(source);
    }

    protected String getResourceAsString() throws Exception {
        return Resources.getResourceAsString(source);
    }

    protected InputStream getResourceAsStream() throws Exception {
        return Resources.getResourceAsStream(source);
    }

    protected byte[] getResourceAsByteArray() throws Exception {
        return Resources.getResourceAsByteArray(source);
    }
}
