package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

import java.util.Base64;

@Command(name = "base64-decode", uri = "resource://base64-decode")
public class Base64DecodeCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        return new String(Base64.getDecoder().decode(contents()));
    }
}
