package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.ShowNoteCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "show-note")
public class ShowNoteTask extends GitTask<ShowNoteCommand>{
}
