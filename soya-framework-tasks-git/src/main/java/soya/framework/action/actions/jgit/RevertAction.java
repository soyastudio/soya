package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RevertCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "revert")
public class RevertAction extends GitAction<RevertCommand> {
}
