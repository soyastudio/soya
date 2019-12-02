package soya.framework.dovetails.component.ant.adapters.git;

import com.google.gson.JsonElement;
import com.rimerosolutions.ant.git.AbstractGitTask;
import org.eclipse.jgit.lib.ProgressMonitor;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.adapters.AntTaskAdapterSupport;

public abstract class GitTaskAdapter<T extends AbstractGitTask> extends AntTaskAdapterSupport<T> {

    protected String uri;
    protected String dir;

    protected ProgressMonitor progressMonitor;
    protected String unlessCondition;
    protected String ifCondition;
    protected String settingsRef;

    public GitTaskAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(T task, TaskSession session) {
        task.setUri(uri);
        task.setDirectory(getDir(dir));

    }
}
