package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Echo;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "echo", attributes = {"message"})
public class EchoAdapter extends AntTaskAdapterSupport<Echo> {
    private String message;

    public EchoAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Echo task, TaskSession session) {
        task.setMessage(message);
    }
}
