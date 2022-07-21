package soya.framework.action.actions.reflect;

import soya.framework.action.*;

import java.net.URI;
import java.security.Signature;

@Command(group = "reflect", name = "signature", httpMethod = Command.HttpMethod.GET)
public class ActionSignatureAction extends ReflectionAction<String> {

    @CommandOption(option = "a", required = true)
    private String action;

    @Override
    protected String execute() throws Exception {
        String uri = action.trim();
        if (uri.indexOf(32) > 0) {
            uri = uri.substring(0, uri.indexOf(32));
        }

        if (uri.indexOf("://") > 0) {
            return ActionSignature.builder(uri).create().toString();

        } else {
            return ActionSignature.builder(ActionClass.get((Class<? extends ActionCallable>) Class.forName(uri))).create().toURI().toString();

        }
    }
}
