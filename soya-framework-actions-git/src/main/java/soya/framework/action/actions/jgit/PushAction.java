package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.PushCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "push")
public class PushAction extends GitAction<PushCommand> {
}
