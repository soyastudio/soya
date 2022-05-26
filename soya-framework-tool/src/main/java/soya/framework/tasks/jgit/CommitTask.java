package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.CommitCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "commit")
public class CommitTask extends GitTask<CommitCommand> {
}
