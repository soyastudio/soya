package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ListBranchCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "list-branch")
public class ListBranchAction extends GitAction<ListBranchCommand> {
}
