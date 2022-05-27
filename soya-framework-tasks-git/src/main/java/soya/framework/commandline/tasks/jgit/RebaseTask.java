package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RebaseCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "rebase")
public class RebaseTask extends GitTask<RebaseCommand>{
}
