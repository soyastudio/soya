package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.StashCreateCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "stash-create")
public class StashCreateAction extends GitAction<StashCreateCommand> {
}
