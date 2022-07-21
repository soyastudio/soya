package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.StashListCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "stash-list")
public class StashListAction extends GitAction<StashListCommand> {
}
