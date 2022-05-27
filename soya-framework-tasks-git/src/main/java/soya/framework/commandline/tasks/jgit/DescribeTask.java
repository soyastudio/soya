package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.DescribeCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "describe")
public class DescribeTask extends GitTask<DescribeCommand> {
}
