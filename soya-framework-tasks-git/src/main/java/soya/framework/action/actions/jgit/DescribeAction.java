package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.DescribeCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "describe")
public class DescribeAction extends GitAction<DescribeCommand> {
}
