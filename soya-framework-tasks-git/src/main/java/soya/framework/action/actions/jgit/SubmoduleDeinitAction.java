package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleDeinitCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-deinit")
public class SubmoduleDeinitAction extends GitAction<SubmoduleDeinitCommand> {
}
