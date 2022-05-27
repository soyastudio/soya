package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RenameBranchCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "rename-branch")
public class RenameBranchTask extends GitTask<RenameBranchCommand> {
}
