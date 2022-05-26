package soya.framework.commandline.tasks.text;

import soya.framework.commandline.Command;

@Command(group = "text-util", name = "aes-decrypt")
public class AESDecryptTask extends AESTask {

    @Override
    protected String execute() throws Exception {
        return decrypt(source, secret);
    }
}
