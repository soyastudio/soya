package soya.framework.action.actions.reflect;

import soya.framework.action.*;

import java.net.URI;

@Command(group = "reflect", name = "uri-template", httpMethod = Command.HttpMethod.GET)
public class ActionCommandLineTemplateAction extends ReflectionAction<String> {

    @CommandOption(option = "a", required = true)
    private String action;

    @Override
    protected String execute() throws Exception {
        String uri = action.trim();
        if (uri.indexOf(32) > 0) {
            uri = uri.substring(0, uri.indexOf(32));
        }

        if (uri.indexOf("://") > 0) {
            return ActionCommandLine.builder(ActionContext
                            .getInstance()
                            .getActionType(ActionName
                                    .fromURI(URI.create(uri))))
                    .create().toURI();

        } else {
            return ActionCommandLine.builder((Class<? extends ActionCallable>) Class.forName(uri)).create().toURI();

        }
    }
}
