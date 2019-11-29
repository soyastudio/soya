package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Unpack;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public abstract class UnpackAdapter<T extends Unpack> extends AntTaskAdapterSupport<T> {

    protected String src;
    protected String dest;

    public UnpackAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(T task, TaskSession session) {
        task.setDest(getFile(dest));
        task.setSrc(getFile(src));
    }
}
