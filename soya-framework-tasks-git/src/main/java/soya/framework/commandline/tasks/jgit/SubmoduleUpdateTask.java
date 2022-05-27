package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleUpdateCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-update")
public class SubmoduleUpdateTask extends GitTask<SubmoduleUpdateCommand>{
}
