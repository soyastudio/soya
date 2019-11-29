package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;

@AntTaskDef(name = "exec", attributes = {"os", "dir", "executable"})
public class ExecAdapter extends AntTaskAdapterSupport<ExecTask> {

    private String os;
    private String osFamily;
    private String dir;
    private String executable;

    protected boolean failOnError = false;
    protected boolean newEnvironment = false;
    private Long timeout = null;

    public ExecAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(ExecTask task, TaskSession session) {
        task.setProject(new Project());
        String ops = os != null ? os : System.getProperty("os.name");
        task.setOs(ops);
        // task.setOsFamily(osFamily);
        task.setExecutable(executable);
        if (dir != null) {
            task.setDir(new File(dir));
        }
    }
}
