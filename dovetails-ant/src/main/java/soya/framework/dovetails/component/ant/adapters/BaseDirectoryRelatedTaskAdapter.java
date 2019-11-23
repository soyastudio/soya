package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskAdapter;

import java.io.File;

public abstract class BaseDirectoryRelatedTaskAdapter<T extends Task> extends AntTaskAdapter<T> {
    private transient File _baseDir;

    public BaseDirectoryRelatedTaskAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
        this._baseDir = context.getExternalContext().getBaseDir();
    }

    @Override
    protected void preExecute(TaskSession session) {

        if (session.get(TaskSession.CURRENT_DIRECTORY) != null) {
            _baseDir = (File) session.get(TaskSession.CURRENT_DIRECTORY);
        }
    }

    protected File getDir(String dir) {
        if (dir == null || dir.trim().length() == 0) {
            return getBaseDir();

        } else if (FileUtils.isAbsolutePath(dir)) {
            return new File(dir);

        } else {

            return new File(getBaseDir(), dir);
        }
    }

    protected File getBaseDir() {
        return _baseDir;
    }
}
