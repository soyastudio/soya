package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.CheckoutCommand;
import soya.framework.action.Command;

@Command(group = "git", name = "checkout")
public class CheckoutAction extends GitAction<CheckoutCommand> {
}
