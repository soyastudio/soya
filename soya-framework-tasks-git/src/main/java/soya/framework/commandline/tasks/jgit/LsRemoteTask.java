package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.LsRemoteCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "ls-remote")
public class LsRemoteTask extends GitTask<LsRemoteCommand> {
}
