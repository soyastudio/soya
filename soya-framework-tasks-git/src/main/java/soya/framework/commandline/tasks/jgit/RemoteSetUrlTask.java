package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RemoteSetUrlCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "remote-set-url")
public class RemoteSetUrlTask extends GitTask<RemoteSetUrlCommand> {
}
