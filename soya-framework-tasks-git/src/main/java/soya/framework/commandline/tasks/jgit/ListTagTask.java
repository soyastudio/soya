package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ListTagCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "list-tag")
public class ListTagTask extends GitTask<ListTagCommand> {
}
