package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.AddCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "add")
public class AddAction extends GitAction<AddCommand> {

}
