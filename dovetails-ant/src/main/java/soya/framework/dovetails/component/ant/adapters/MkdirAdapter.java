package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "mkdir", attributes = {"dir", "cd"})
public class MkdirAdapter extends AntTaskAdapterSupport<Mkdir> {
    private String dir;
    private boolean cd;

    public MkdirAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void postExecute(TaskSession session) {
        if (cd) {
            session.set(TaskSession.CURRENT_DIRECTORY, getDir(dir));
        }
    }

    @Override
    protected void init(Mkdir task, TaskSession session) {
        task.setDir(getDir(dir));
    }
}
