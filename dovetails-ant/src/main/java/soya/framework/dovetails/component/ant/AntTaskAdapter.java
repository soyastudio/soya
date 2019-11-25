package soya.framework.dovetails.component.ant;

import org.apache.tools.ant.Task;
import soya.framework.dovetails.TaskSession;

public interface AntTaskAdapter<T extends Task> {
    void execute(TaskSession session) throws AdapterException;
}
