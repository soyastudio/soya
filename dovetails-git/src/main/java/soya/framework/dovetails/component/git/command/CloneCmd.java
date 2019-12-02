package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.CloneCommand;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;

public class CloneCmd extends GitCmd<CloneCommand> {

    @Override
    protected CloneCommand create(TaskSession session) {
        return null;
    }

}
