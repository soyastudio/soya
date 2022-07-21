package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.StashApplyCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "stash-apply")
public class StashApplyAction extends GitAction<StashApplyCommand> {
}
