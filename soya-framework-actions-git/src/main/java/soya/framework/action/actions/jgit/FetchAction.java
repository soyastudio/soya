package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.FetchCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "fetch")
public class FetchAction extends GitAction<FetchCommand> {
}
