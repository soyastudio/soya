package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.CreateBranchCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "create-branch")
public class CreateBranchTask extends GitTask<CreateBranchCommand> {
}
