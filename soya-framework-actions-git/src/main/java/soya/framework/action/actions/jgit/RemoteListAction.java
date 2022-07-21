package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RemoteListCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "remote-list")
public class RemoteListAction extends GitAction<RemoteListCommand> {
}
