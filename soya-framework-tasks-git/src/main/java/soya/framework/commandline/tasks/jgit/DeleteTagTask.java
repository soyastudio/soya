package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.DeleteTagCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "delete-tag")
public class DeleteTagTask extends GitTask<DeleteTagCommand> {
}
