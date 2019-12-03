package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Ant;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "ant", attributes = {"dir", "antFile", "target"})
public class AntAdapter extends AntTaskAdapterSupport<Ant> {

    private String dir;
    private String antFile;
    private String target;

    public AntAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Ant task, TaskSession session) {
        task.setDir(getDir(dir));
        task.setAntfile(antFile == null ? "build.xml" : antFile);
        task.setTarget(target);
    }
}
