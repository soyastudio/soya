package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.TagCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "tag")
public class TagTask extends GitTask<TagCommand>{
}
