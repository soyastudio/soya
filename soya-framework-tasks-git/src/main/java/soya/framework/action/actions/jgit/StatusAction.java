package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.StatusCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "status")
public class StatusAction extends GitAction<StatusCommand> {
}
