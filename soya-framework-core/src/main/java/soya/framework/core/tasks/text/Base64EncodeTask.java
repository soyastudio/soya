package soya.framework.core.tasks.text;

import soya.framework.core.Command;

import java.util.Base64;

@Command(group = "text-util", name = "base64-encode")
public class Base64EncodeTask extends TextUtilTask {

    @Override
    protected String execute() throws Exception {
        return Base64.getEncoder().encodeToString(source.getBytes(encoding));

    }

}
