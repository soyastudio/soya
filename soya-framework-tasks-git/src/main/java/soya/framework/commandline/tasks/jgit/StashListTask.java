package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.StashListCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "stash-list")
public class StashListTask extends GitTask<StashListCommand>{
}
