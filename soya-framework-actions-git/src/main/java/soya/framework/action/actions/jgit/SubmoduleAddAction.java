package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleAddCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-add")
public class SubmoduleAddAction extends GitAction<SubmoduleAddCommand> {
}
