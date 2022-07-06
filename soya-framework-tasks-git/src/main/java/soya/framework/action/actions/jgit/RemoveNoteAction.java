package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.RemoveNoteCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "remove-note")
public class RemoveNoteAction extends GitAction<RemoveNoteCommand> {

}