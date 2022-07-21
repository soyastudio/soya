package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleSyncCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-sync")
public class SubmoduleSyncAction extends GitAction<SubmoduleSyncCommand> {
}
