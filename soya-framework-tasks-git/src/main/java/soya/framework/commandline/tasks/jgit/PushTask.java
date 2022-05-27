package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.PushCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "push")
public class PushTask extends GitTask<PushCommand> {
}
