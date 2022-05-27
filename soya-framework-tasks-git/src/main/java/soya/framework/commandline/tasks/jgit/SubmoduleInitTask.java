package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleInitCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-init")
public class SubmoduleInitTask extends GitTask<SubmoduleInitCommand>{
}
