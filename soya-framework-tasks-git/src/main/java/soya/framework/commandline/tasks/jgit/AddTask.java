package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.AddCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "add")
public class AddTask extends GitTask<AddCommand> {

}
