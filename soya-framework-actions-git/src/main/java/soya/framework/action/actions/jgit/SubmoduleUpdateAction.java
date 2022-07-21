package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.SubmoduleUpdateCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "sub-module-update")
public class SubmoduleUpdateAction extends GitAction<SubmoduleUpdateCommand> {
}
