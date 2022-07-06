package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.CreateBranchCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "create-branch")
public class CreateBranchAction extends GitAction<CreateBranchCommand> {
}
