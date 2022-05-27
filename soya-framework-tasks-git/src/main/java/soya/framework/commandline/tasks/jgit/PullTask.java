package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.PullCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "pull")
public class PullTask extends GitTask<PullCommand>{
}
