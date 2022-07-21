package soya.framework.action.actions.text;

import soya.framework.action.Command;

@Command(group = "text-util", name = "aes-encrypt")
public class AESEncryptAction extends AESAction {

    @Override
    protected String execute() throws Exception {
        return encrypt(source, secret);
    }
}
