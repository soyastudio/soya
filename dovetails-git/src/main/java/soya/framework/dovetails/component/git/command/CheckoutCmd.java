package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.CheckoutCommand;
import soya.framework.dovetails.PropertyDef;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;

public class CheckoutCmd extends GitCmd<CheckoutCommand> {

    @PropertyDef
    private String branchName;

    @PropertyDef
    private String startPoint;

    @PropertyDef
    private boolean createBranch = false;

    @PropertyDef
    private boolean trackBranchOnCreate = true;

    @Override
    protected CheckoutCommand create(TaskSession session) {


        return null;
    }
}
