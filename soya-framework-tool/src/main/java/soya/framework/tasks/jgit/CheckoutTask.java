package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.CheckoutCommand;
import soya.framework.commandline.Command;

@Command(group = "git", name = "checkout")
public class CheckoutTask extends GitTask<CheckoutCommand>{
}
