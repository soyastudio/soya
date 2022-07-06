package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RemoteSetUrlCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "remote-set-url")
public class RemoteSetUrlAction extends GitAction<RemoteSetUrlCommand> {
}
