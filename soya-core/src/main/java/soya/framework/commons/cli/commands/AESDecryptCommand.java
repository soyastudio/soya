package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

@Command(name = "aes-decrypt", uri = "resource://aes-decrypt")
public class AESDecryptCommand extends AESCommand {

    @Override
    public String call() throws Exception {
        return decrypt(contents(), secret);
    }
}
