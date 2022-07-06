package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleInitCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-init")
public class SubmoduleInitAction extends GitAction<SubmoduleInitCommand> {
}
