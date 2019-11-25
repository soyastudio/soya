package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskAdapter;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;

@AntTaskDef(name = "apply", attributes = {"os", "dir", "executable"})
public class ApplyAdapter extends AntTaskAdapterSupport<ExecuteOn> {
    private String os;
    private String dir;
    private String executable;

    public ApplyAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(ExecuteOn task, TaskSession session) {

        String ops = os != null ? os : System.getProperty("os.name");
        task.setOs(ops);
        // task.setOsFamily(osFamily);
        task.setExecutable(executable);
        if (dir != null) {
            task.setDir(new File(dir));
        }
    }
}
