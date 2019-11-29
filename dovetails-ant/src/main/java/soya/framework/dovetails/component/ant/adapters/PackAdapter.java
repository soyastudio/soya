package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Pack;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public abstract class PackAdapter<T extends Pack> extends AntTaskAdapterSupport<T> {
    protected String src;
    protected String destFile;

    public PackAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(T task, TaskSession session) {
        task.setSrc(getFile(src));
        task.setDestfile(getFile(destFile));
    }
}
