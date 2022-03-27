package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

@Command(group = "resource", name = "aes-encrypt")
public class AESEncryptCommand extends AESCommand {
    @Override
    public String call() throws Exception {
        return encrypt(contents(), secret);
    }
}
