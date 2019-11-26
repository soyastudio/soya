package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Expand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

public abstract class ExpandAdapter<T extends Expand> extends AntTaskAdapterSupport<T> {

    protected String dest;
    protected String source;
    protected boolean overwrite = true;
    protected String encoding;
    protected boolean failOnEmptyArchive;
    protected boolean stripAbsolutePathSpec = true;
    protected boolean scanForUnicodeExtraFields = true;
    protected Boolean allowFilesToEscapeDest = null;

    public ExpandAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Expand task, TaskSession session) {
        task.setDest(getDir(dest));
        task.setSrc(getFile(source));
        task.setOverwrite(overwrite);
        task.setFailOnEmptyArchive(failOnEmptyArchive);
        task.setScanForUnicodeExtraFields(scanForUnicodeExtraFields);
        task.setStripAbsolutePathSpec(stripAbsolutePathSpec);
        if (allowFilesToEscapeDest != null) {
            task.setAllowFilesToEscapeDest(allowFilesToEscapeDest);
        }

        if (encoding != null) {
            task.setEncoding(encoding);
        }
    }
}
