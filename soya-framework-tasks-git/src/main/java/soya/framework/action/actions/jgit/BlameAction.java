package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.BlameCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "blame")
public class BlameAction extends GitAction<BlameCommand> {
}
