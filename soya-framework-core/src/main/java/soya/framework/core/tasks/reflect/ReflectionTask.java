package soya.framework.core.tasks.reflect;

import soya.framework.core.CommandGroup;
import soya.framework.core.Task;

@CommandGroup(group = "reflect", title = "Reflection Service", description = "Toolkit for reflecting SOYA command metadata and system runtime information.")
public abstract class ReflectionTask<T> extends Task<T> {

}
