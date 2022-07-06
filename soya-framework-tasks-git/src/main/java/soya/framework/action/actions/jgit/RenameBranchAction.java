package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RenameBranchCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "rename-branch")
public class RenameBranchAction extends GitAction<RenameBranchCommand> {
}
