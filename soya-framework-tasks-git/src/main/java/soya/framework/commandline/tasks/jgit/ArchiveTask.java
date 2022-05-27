package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ArchiveCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "archive")
public class ArchiveTask extends GitTask<ArchiveCommand> {
}
