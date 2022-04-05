package soya.framework.core.commands.text;

import soya.framework.core.Command;
import soya.framework.core.commands.ResourceCommand;

import java.nio.charset.Charset;
import java.util.Base64;

@Command(group = "text-util", name = "base64-encode")
public class Base64EncodeCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        return Base64.getEncoder().encodeToString(contents().getBytes(Charset.defaultCharset()));
    }
}
