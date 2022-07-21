package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RemoteRemoveCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "remote-remove")
public class RemoteRemoveAction extends GitAction<RemoteRemoveCommand> {
}
