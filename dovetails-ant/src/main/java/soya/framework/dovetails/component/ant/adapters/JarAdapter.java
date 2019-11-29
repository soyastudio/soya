package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Jar;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "jar", attributes = {"destFile", "baseDir", "encoding", "includes",
        "excludes", "manifestFile", "filesOnly"})
public class JarAdapter extends BaseZipAdapter<Jar> {

    private String manifestFile;

    public JarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Jar task, TaskSession session) {
        super.init(task, session);
        if (manifestFile != null) {
            task.setManifest(getFile(manifestFile));
        }
    }
}
