package soya.framework.commandline.tasks.jgit;

import org.eclipse.jgit.api.VerifySignatureCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "verify-signature")
public class VerifySignatureTask extends GitTask<VerifySignatureCommand>{
}
