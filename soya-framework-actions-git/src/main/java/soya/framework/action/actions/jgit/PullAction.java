package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.PullCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "pull")
public class PullAction extends GitAction<PullCommand> {
}
