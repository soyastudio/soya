package soya.framework.document;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandGroup;

@CommandGroup(group = "document", title = "Document Processing Tool", description = "")
public interface DocumentCommand<T> extends CommandCallable<T> {
}
