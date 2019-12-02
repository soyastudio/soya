package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.RemoteRemoveCommand;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;

public class RemoveCmd extends GitCmd<RemoteRemoveCommand> {
    @Override
    protected RemoteRemoveCommand create(TaskSession session) {
        return null;
    }
}
