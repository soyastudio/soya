package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.VerifySignatureCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "verify-signature")
public class VerifySignatureAction extends GitAction<VerifySignatureCommand> {
}
