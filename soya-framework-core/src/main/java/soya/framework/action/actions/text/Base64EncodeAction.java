package soya.framework.action.actions.text;

import soya.framework.action.Command;

import java.util.Base64;

@Command(group = "text-util", name = "base64-encode")
public class Base64EncodeAction extends TextUtilAction {

    @Override
    protected String execute() throws Exception {
        return Base64.getEncoder().encodeToString(source.getBytes(encoding));

    }

}
