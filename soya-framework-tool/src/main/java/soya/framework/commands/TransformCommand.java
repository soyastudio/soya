package soya.framework.commands;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandGroup;

@CommandGroup(group = "transform", title = "Data Conversion and Transformation", description = "")
public interface TransformCommand<T> extends CommandCallable<T> {
}
