package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.MergeCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "merge")
public class MergeTask extends GitTask<MergeCommand> {

}
