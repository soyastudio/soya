package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

import java.nio.charset.Charset;
import java.util.Base64;

@Command(name = "base64-encode", uri = "resource://base64-encode")
public class Base64EncodeCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        return Base64.getEncoder().encodeToString(contents().getBytes(Charset.defaultCharset()));
    }
}
