package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ReflogCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "reflog")
public class ReflogAction extends GitAction<ReflogCommand> {
}
