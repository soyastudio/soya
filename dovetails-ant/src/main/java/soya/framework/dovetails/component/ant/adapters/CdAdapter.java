package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;

@AntTaskDef(name = "cd", attributes = {"dir"})
public class CdAdapter extends BaseDirectoryRelatedTaskAdapter<CdAdapter.Cd> {

    private String dir;

    public CdAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected Cd createAntTask(ProcessContext context) {
        Cd task = new Cd();
        return task;
    }

    @Override
    protected void postExecute(TaskSession session) {
        super.postExecute(session);
        File currentDir = getDir(dir);
        session.set(TaskSession.CURRENT_DIRECTORY, currentDir);
    }

    public static class Cd extends Task {
        private File dir;

    }
}
