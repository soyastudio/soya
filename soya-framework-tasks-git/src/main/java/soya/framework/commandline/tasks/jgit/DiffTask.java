package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.DiffCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "diff")
public class DiffTask extends GitTask<DiffCommand> {
}
