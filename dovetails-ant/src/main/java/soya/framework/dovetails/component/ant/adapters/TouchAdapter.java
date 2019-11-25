package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Touch;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "touch", attributes = {"file", "millis", "datetime", "pattern", "mkdirs", "verbose"})
public class TouchAdapter extends AntTaskAdapterSupport<Touch> {
    private String file;
    private long millis = -1L;
    private String datetime;
    private String pattern;
    private boolean mkdirs;
    private boolean verbose;

    public TouchAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Touch task, TaskSession session) {
        task.setFile(getFile(file));
        task.setMillis(millis);
        task.setDatetime(datetime);
        task.setPattern(pattern);
        task.setMkdirs(mkdirs);
        task.setVerbose(verbose);
    }
}
