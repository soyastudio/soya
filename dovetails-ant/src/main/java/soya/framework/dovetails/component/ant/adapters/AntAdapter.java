package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "ant", attributes = {"dir", "antFile", "target"})
public class AntAdapter extends BaseDirectoryRelatedTaskAdapter<Ant> {

    private String dir;

    private String antFile;

    private String target;

    public AntAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected Ant createAntTask(ProcessContext context) {
        Ant task = new Ant();
        task.setProject(new Project());
        task.setDir(getDir(dir));
        task.setAntfile(antFile == null ? "build.xml" : antFile);
        task.setTarget(target);
        return task;
    }
}
