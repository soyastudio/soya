package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RevertCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "revert")
public class RevertTask extends GitTask<RevertCommand> {
}
