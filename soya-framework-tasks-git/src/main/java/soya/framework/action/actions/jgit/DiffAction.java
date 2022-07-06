package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.DiffCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "diff")
public class DiffAction extends GitAction<DiffCommand> {
}
