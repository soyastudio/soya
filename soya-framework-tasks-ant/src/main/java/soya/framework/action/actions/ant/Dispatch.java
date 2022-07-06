package soya.framework.action.actions.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import soya.framework.action.*;

public class Dispatch extends AntTaskExtension {

    private String name;
    private String uri;
    private Commandline commandline = new Commandline();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Commandline.Argument createArg() {
        return commandline.createArgument();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void execute() throws BuildException {
        try {
            Class<? extends ActionCallable> cls = ActionContext.getInstance().getActionType(ActionName.fromURI(uri));
            ActionCallable task = ActionParser.create(cls, commandline.getArguments());

            ActionResult result = task.call();
            if (name != null) {
                getProject().setResult(name, result.result());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
