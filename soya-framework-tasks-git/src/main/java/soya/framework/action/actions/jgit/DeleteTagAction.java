package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.DeleteTagCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "delete-tag")
public class DeleteTagAction extends GitAction<DeleteTagCommand> {
}
