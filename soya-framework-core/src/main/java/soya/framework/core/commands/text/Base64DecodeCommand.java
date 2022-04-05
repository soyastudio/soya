package soya.framework.core.commands.text;

import soya.framework.core.Command;
import soya.framework.core.commands.ResourceCommand;

import java.util.Base64;

@Command(group = "text-util", name = "base64-decode")
public class Base64DecodeCommand extends ResourceCommand {

    @Override
    public String call() throws Exception {
        return new String(Base64.getDecoder().decode(contents()));
    }
}
