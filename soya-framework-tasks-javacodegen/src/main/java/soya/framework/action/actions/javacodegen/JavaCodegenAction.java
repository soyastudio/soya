package soya.framework.action.actions.javacodegen;

import soya.framework.action.Action;
import soya.framework.action.CommandOption;

public abstract class JavaCodegenAction extends Action<String> {

    @CommandOption(option = "p")
    protected String packageName;

    @CommandOption(option = "c")
    protected String className = "MyClass";

    @CommandOption(option = "i")
    protected String imports;

}
