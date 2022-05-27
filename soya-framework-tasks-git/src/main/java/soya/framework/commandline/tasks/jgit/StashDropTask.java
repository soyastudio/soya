package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.StashDropCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "stash-drop")
public class StashDropTask extends GitTask<StashDropCommand> {
}
