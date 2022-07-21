package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.DeleteBranchCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "delete-branch")
public class DeleteBranchAction extends GitAction<DeleteBranchCommand> {
}
