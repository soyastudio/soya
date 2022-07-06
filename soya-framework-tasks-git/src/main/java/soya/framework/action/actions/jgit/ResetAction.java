package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ResetCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "reset")
public class ResetAction extends GitAction<ResetCommand> {
}
