package soya.framework.core.tasks.text;

import soya.framework.core.Command;

@Command(group = "text-util", name = "aes-decrypt")
public class AESDecryptTask extends AESTask {

    @Override
    protected String execute() throws Exception {
        return decrypt(source, secret);
    }
}
