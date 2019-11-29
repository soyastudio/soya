package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Tar;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public class TarAdapter extends AntTaskAdapterSupport<Tar> {
    public TarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Tar task, TaskSession session) {

    }
}
