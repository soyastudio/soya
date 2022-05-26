package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.BlameCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "blame")
public class BlameTask extends GitTask<BlameCommand> {
}
