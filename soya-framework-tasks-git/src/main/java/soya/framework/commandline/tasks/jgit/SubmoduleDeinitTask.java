package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleDeinitCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-deinit")
public class SubmoduleDeinitTask extends GitTask<SubmoduleDeinitCommand> {
}
