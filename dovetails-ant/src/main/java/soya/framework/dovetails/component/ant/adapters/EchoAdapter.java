package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Echo;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskAdapter;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "echo", attributes = {"message"})
public class EchoAdapter extends AntTaskAdapter<Echo> {
    private String message;

    public EchoAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected Echo createAntTask(ProcessContext context) {
        Echo task = new Echo();
        task.setMessage(message);
        return task;
    }
}
