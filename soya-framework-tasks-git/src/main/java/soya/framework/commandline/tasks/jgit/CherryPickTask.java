package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.CherryPickCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "cherry-pick")
public class CherryPickTask extends GitTask<CherryPickCommand> {
}
