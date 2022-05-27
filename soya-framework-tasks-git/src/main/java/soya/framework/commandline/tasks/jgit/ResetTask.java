package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ResetCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "reset")
public class ResetTask extends GitTask<ResetCommand> {
}
