package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RemoteAddCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "remote-add")
public class RemoteAddTask extends GitTask<RemoteAddCommand> {
}
