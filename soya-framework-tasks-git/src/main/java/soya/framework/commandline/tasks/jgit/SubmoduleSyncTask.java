package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.SubmoduleSyncCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "sub-module-sync")
public class SubmoduleSyncTask extends GitTask<SubmoduleSyncCommand>{
}
