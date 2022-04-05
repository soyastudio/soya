package soya.framework.core.commands.text;

import soya.framework.core.Command;

@Command(group = "text-util", name = "aes-encrypt")
public class AESEncryptCommand extends AESCommand {
    @Override
    public String call() throws Exception {
        return encrypt(contents(), secret);
    }
}
