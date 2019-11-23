package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;

@AntTaskDef(name = "mkdir", attributes = {"dir", "cd"})
public class MkdirAdapter extends BaseDirectoryRelatedTaskAdapter<Mkdir> {
    private transient File currentDirectory;

    private String dir;
    private boolean cd;

    public MkdirAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected Mkdir createAntTask(ProcessContext context) {
        Mkdir task = new Mkdir();
        task.setDir(getDir(dir));
        return task;
    }

    private File getBaseDir(String base, ProcessContext context) {
        return context.getExternalContext().getBaseDir();
    }

    @Override
    protected void postExecute(TaskSession session) {
        if(cd) {
            session.set(TaskSession.CURRENT_DIRECTORY, getDir(dir));
        }
    }
}
