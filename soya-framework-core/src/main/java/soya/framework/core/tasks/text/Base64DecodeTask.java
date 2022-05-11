package soya.framework.core.tasks.text;

import soya.framework.core.Command;

import java.util.Base64;

@Command(group = "text-util", name = "base64-decode")
public class Base64DecodeTask extends TextUtilTask {

    @Override
    protected String execute() throws Exception {
        return new String(Base64.getDecoder().decode(source.getBytes()), encoding);
    }
}
