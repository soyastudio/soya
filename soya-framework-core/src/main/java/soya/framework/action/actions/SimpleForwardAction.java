package soya.framework.action.actions;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.dispatch.ActionForward;
import soya.framework.action.dispatch.ActionForwardAction;

@Command(group = "abc", name = "forward")
@ActionForward(command = "text-util://base64-encode?s=arg(message)")
public class SimpleForwardAction extends ActionForwardAction<String> {

    @CommandOption(option = "m", dataForProcessing = true, required = true)
    private String message;
}
