package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ReflogCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "reflog")
public class ReflogTask extends GitTask<ReflogCommand>{
}
