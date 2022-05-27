package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleStatusCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-status")
public class SubmoduleStatusTask extends GitTask<SubmoduleStatusCommand>{
}
