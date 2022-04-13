package soya.framework.commands.transform;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandGroup;

@CommandGroup(group = "transform", title = "Data Transformation Tool", description = "")
public interface TransformCommand<T> extends CommandCallable<T> {
}
