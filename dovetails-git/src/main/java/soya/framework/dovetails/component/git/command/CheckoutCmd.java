package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.CheckoutCommand;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;

public class CheckoutCmd extends GitCmd<CheckoutCommand> {
    @Override
    protected CheckoutCommand create(TaskSession session) {
        return null;
    }
}
