package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.FetchCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "fetch")
public class FetchTask extends GitTask<FetchCommand> {
}
