package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Ear;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "ear", attributes = {"destFile", "manifest", "appxml", "fileSet"})
public class EarAdapter extends BaseZipAdapter<Ear> {
    private String manifest;
    private String appxml;

    public EarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Ear task, TaskSession session) {
        super.init(task, session);
        if (manifest != null) {
            task.setManifest(getFile(manifest));
        }
        task.setAppxml(getFile(appxml));

    }
}
