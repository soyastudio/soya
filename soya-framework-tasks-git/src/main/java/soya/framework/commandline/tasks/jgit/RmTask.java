package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RmCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "rm")
public class RmTask extends GitTask<RmCommand> {
}
