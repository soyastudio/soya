package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RemoteListCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "remote-list")
public class RemoteListTask extends GitTask<RemoteListCommand> {
}
