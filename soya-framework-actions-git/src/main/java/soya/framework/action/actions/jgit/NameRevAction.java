package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.NameRevCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "name-rev")
public class NameRevAction extends GitAction<NameRevCommand> {
}
