package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.StatusCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "status")
public class StatusTask extends GitTask<StatusCommand> {
}
