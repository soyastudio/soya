package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.MergeCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "merge")
public class MergeAction extends GitAction<MergeCommand> {

}
