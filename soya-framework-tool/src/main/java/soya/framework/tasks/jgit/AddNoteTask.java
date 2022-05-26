package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.AddNoteCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "add-note")
public class AddNoteTask extends GitTask<AddNoteCommand> {
}
