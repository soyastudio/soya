package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.CleanCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "clean")
public class CleanTask extends GitTask<CleanCommand> {
}
