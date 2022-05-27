package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ListBranchCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "list-branch")
public class ListBranchTask extends GitTask<ListBranchCommand>{
}
