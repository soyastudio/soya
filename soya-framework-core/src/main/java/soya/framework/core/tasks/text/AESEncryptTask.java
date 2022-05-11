package soya.framework.core.tasks.text;

import soya.framework.core.Command;

@Command(group = "text-util", name = "aes-encrypt")
public class AESEncryptTask extends AESTask{

    @Override
    protected String execute() throws Exception {
        return encrypt(source, secret);
    }
}
