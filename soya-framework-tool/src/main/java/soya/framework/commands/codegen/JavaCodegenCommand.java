package soya.framework.commands.codegen;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;

public abstract class JavaCodegenCommand implements CommandCallable<String> {

    @CommandOption(option = "p")
    protected String packageName;

    @CommandOption(option = "c")
    protected String className = "MyClass";

    @CommandOption(option = "i")
    protected String imports;

}
