package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskAdapter;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;

@AntTaskDef(name = "mkdir", attributes = {"base", "dir", "cd"})
public class MkdirAdapter extends AntTaskAdapter<Mkdir> {
    private transient File currentDirectory;

    private String base = "./";
    private String dir;
    private boolean cd;

    public MkdirAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected Mkdir createAntTask(ProcessContext context) {
        File baseDir = getBaseDir(base, context);
        Mkdir task = new Mkdir();
        task.setDir(new File(baseDir, dir));

        return task;
    }

    private File getBaseDir(String base, ProcessContext context) {
        return context.getExternalContext().getBaseDir();
    }

    @Override
    protected void preExecute(TaskSession session) {
        System.out.println("-------------- set current state");
    }

    @Override
    protected void postExecute(TaskSession session) {
        System.out.println("------- setAsCurrent: " + cd);
    }
}
