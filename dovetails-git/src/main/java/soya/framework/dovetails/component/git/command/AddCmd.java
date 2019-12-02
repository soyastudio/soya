package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.AddCommand;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;

public class AddCmd extends GitCmd<AddCommand> {
    @Override
    protected AddCommand create(TaskSession session) {
        return null;
    }
}
