package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.CommitCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "commit")
public class CommitAction extends GitAction<CommitCommand> {
}
