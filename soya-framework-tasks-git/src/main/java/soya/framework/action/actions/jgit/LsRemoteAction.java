package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.LsRemoteCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "ls-remote")
public class LsRemoteAction extends GitAction<LsRemoteCommand> {
}
