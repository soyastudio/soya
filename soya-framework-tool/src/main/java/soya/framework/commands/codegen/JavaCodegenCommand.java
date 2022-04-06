package soya.framework.commands.codegen;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;

public abstract class JavaCodegenCommand implements CommandCallable<String> {

    @CommandOption(option = "p", longOption = "pkg")
    protected String packageName;

    @CommandOption(option = "c", longOption = "cls")
    protected String className = "MyClass";

    @CommandOption(option = "i", longOption = "imp")
    protected String imports;

}
