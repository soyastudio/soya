package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Zip;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public abstract class BaseZipAdapter<T extends Zip> extends AntTaskAdapterSupport<T> {

    protected String destFile;
    protected String baseDir;
    protected String encoding;
    protected String includes;
    protected String excludes;
    protected boolean filesOnly;

    protected FileSetModel[] fileSet;

    public BaseZipAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(T task, TaskSession session) {
        task.setDestFile(getFile(destFile));
        task.setBasedir(getDir(baseDir));
        if (encoding != null) {
            task.setEncoding(encoding);
        }
        task.setFilesonly(filesOnly);

        task.setIncludes(includes);
        task.setExcludes(excludes);

        if(fileSet != null && fileSet.length > 0) {
            for(FileSetModel model: fileSet) {
                task.addFileset(getFileSet(model));
            }
        }
    }
}
