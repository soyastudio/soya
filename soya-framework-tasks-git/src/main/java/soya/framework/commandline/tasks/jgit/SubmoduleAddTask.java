package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleAddCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-add")
public class SubmoduleAddTask extends GitTask<SubmoduleAddCommand> {
}
