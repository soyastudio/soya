package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RemoteRemoveCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "remote-remove")
public class RemoteRemoveTask extends GitTask<RemoteRemoveCommand> {
}
