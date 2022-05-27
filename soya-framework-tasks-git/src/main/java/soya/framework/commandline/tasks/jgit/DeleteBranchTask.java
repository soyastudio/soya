package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.DeleteBranchCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "delete-branch")
public class DeleteBranchTask extends GitTask<DeleteBranchCommand> {
}
