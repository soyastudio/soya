package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.RemoveNoteCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "remove-note")
public class RemoveNoteTask extends GitTask<RemoveNoteCommand> {

}