package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.ListNotesCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "list-notes")
public class ListNotesAction extends GitAction<ListNotesCommand> {

}