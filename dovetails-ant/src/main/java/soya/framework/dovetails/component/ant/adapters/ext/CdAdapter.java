package soya.framework.dovetails.component.ant.adapters.ext;

import com.google.gson.JsonElement;
import org.apache.tools.ant.BuildException;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;
import soya.framework.dovetails.component.ant.adapters.AntTaskAdapterSupport;
import soya.framework.dovetails.component.ant.adapters.SessionAwareAntTask;

import java.io.File;

@AntTaskDef(name = "cd", attributes = {"dir"})
public class CdAdapter extends AntTaskAdapterSupport<CdAdapter.Cd> {

    private String dir;

    public CdAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Cd task, TaskSession session) {
        task.setDir(getDir(dir));
    }

    public static class Cd extends SessionAwareAntTask {
        private File dir;

        public void setDir(File dir) {
            this.dir = dir;
        }

        @Override
        public void execute() throws BuildException {
            session.set(TaskSession.CURRENT_DIRECTORY, dir);

            System.out.println("------------ current dir: " + session.get(TaskSession.CURRENT_DIRECTORY));
        }
    }
}
