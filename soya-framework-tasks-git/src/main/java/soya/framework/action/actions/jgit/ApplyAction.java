package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ApplyCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "apply")
public class ApplyAction extends GitAction<ApplyCommand> {
}
