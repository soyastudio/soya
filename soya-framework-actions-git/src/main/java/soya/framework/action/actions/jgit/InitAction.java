package soya.framework.action.actions.jgit;

import soya.framework.action.Command;
import soya.framework.action.ActionResult;

@Command(group = "git", name = "init", httpMethod = Command.HttpMethod.POST)
public class InitAction extends GitAction {

    @Override
    public ActionResult call() throws Exception {

        return null;
    }
}
