package soya.framework.action.actions.text;

import soya.framework.action.Command;

@Command(group = "text-util", name = "aes-decrypt")
public class AESDecryptAction extends AESAction {

    @Override
    protected String execute() throws Exception {
        return decrypt(source, secret);
    }
}
