package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.CherryPickCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "cherry-pick")
public class CherryPickAction extends GitAction<CherryPickCommand> {
}
