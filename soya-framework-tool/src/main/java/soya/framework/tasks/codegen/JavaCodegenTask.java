package soya.framework.tasks.codegen;

import soya.framework.commandline.Task;
import soya.framework.commandline.CommandOption;

public abstract class JavaCodegenTask extends Task<String> {

    @CommandOption(option = "p")
    protected String packageName;

    @CommandOption(option = "c")
    protected String className = "MyClass";

    @CommandOption(option = "i")
    protected String imports;

}
