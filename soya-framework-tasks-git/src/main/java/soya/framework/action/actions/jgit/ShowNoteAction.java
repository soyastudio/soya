package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ShowNoteCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "show-note")
public class ShowNoteAction extends GitAction<ShowNoteCommand> {
}
