package soya.framework.core.commands.text;

import soya.framework.core.Command;

@Command(group = "text-util", name = "aes-decrypt")
public class AESDecryptCommand extends AESCommand {

    @Override
    public String call() throws Exception {
        return decrypt(contents(), secret);
    }
}
