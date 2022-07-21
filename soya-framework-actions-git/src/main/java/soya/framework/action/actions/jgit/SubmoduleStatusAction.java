package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleStatusCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-status")
public class SubmoduleStatusAction extends GitAction<SubmoduleStatusCommand> {
}
