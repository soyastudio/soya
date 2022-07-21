package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.StashDropCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "stash-drop")
public class StashDropAction extends GitAction<StashDropCommand> {
}
