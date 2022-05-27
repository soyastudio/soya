package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.GarbageCollectCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "garbage-collect")
public class GarbageCollectTask extends GitTask<GarbageCollectCommand> {
}
