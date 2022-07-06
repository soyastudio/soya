package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.LogCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "log")
public class LogAction extends GitAction<LogCommand> {
}
