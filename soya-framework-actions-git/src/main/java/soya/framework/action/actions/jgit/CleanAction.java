package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.CleanCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "clean")
public class CleanAction extends GitAction<CleanCommand> {
}
