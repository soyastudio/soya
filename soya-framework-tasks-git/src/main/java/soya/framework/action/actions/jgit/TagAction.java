package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.TagCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "tag")
public class TagAction extends GitAction<TagCommand> {
}
