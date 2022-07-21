package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.GarbageCollectCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "garbage-collect")
public class GarbageCollectAction extends GitAction<GarbageCollectCommand> {
}
