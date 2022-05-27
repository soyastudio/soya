package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.StashCreateCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "stash-create")
public class StashCreateTask extends GitTask<StashCreateCommand> {
}
