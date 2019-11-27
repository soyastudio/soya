package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Zip;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "zip", attributes = {"destFile", "baseDir", "encoding", "includes",
        "excludes", "filesOnly"})
public class ZipAdapter extends AntTaskAdapterSupport<Zip> {

    protected String destFile;
    protected String baseDir;
    protected String encoding;
    protected String includes;
    protected String excludes;
    protected boolean filesOnly;

    public ZipAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Zip task, TaskSession session) {
        task.setDestFile(getFile(destFile));
        task.setBasedir(getDir(baseDir));
        if (encoding != null) {
            task.setEncoding(encoding);
        }
        task.setFilesonly(filesOnly);

        task.setIncludes(includes);
        task.setExcludes(excludes);
    }
}
