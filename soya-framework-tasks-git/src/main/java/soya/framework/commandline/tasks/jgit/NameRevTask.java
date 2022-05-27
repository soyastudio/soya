package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.NameRevCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "name-rev")
public class NameRevTask extends GitTask<NameRevCommand>{
}
