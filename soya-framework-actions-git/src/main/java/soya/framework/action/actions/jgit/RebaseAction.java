package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RebaseCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "rebase")
public class RebaseAction extends GitAction<RebaseCommand> {
}
