package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.ApplyCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "apply")
public class ApplyTask extends GitTask<ApplyCommand> {
}
