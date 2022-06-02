package soya.framework.commandline.tasks.reflect;

import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.Task;

@CommandGroup(group = "reflect", title = "Reflection Commands", description = "Commands for reflecting system runtime information and command metadata.")
public abstract class ReflectionTask<T> extends Task<T> {

}
