package soya.framework.commandline.tasks.reflect;

import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.Task;

@CommandGroup(group = "reflect", title = "Reflection Service", description = "Toolkit for reflecting SOYA command metadata and system runtime information.")
public abstract class ReflectionTask<T> extends Task<T> {

}
