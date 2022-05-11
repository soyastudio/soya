package soya.framework.tasks.codegen;

import soya.framework.core.Task;
import soya.framework.core.CommandOption;

public abstract class JavaCodegenTask extends Task<String> {

    @CommandOption(option = "p")
    protected String packageName;

    @CommandOption(option = "c")
    protected String className = "MyClass";

    @CommandOption(option = "i")
    protected String imports;

}
