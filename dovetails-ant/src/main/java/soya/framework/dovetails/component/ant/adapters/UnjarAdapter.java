package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Expand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public class UnjarAdapter extends AntTaskAdapterSupport<Expand> {
    public UnjarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Expand task, TaskSession session) {

    }
}
