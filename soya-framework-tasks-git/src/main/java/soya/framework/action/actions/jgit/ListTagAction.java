package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ListTagCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "list-tag")
public class ListTagAction extends GitAction<ListTagCommand> {
}
