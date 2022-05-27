package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ListNotesCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "list-notes")
public class ListNotesTask extends GitTask<ListNotesCommand> {

}