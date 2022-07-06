package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RmCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "rm")
public class RmAction extends GitAction<RmCommand> {
}
