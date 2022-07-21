package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ArchiveCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "archive")
public class ArchiveAction extends GitAction<ArchiveCommand> {
}
