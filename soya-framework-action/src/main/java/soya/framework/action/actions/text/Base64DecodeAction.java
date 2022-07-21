package soya.framework.action.actions.text;

import soya.framework.action.Command;

import java.util.Base64;

@Command(group = "text-util", name = "base64-decode")
public class Base64DecodeAction extends TextUtilAction {

    @Override
    protected String execute() throws Exception {
        return new String(Base64.getDecoder().decode(source.getBytes()), encoding);
    }
}
