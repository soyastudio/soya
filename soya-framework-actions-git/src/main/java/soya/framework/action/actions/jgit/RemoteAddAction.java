package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RemoteAddCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "remote-add")
public class RemoteAddAction extends GitAction<RemoteAddCommand> {
}
