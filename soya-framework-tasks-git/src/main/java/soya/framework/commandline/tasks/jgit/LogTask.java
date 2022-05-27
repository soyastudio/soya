package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.LogCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "log")
public class LogTask extends GitTask<LogCommand>{
}
