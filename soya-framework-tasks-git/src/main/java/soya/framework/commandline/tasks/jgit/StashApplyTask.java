package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.StashApplyCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "stash-apply")
public class StashApplyTask extends GitTask<StashApplyCommand>{
}
