package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Sleep;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskAdapter;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "sleep", attributes = {"failOnError", "hours", "minutes", "seconds", "milliseconds"})
public class SleepAdapter extends AntTaskAdapterSupport<Sleep> {

    private boolean failOnError = true;
    private int seconds = 0;
    private int hours = 0;
    private int minutes = 0;
    private int milliseconds = 0;

    public SleepAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Sleep task, TaskSession session) {
        task.setFailOnError(failOnError);
        task.setHours(hours);
        task.setMinutes(minutes);
        task.setSeconds(seconds);
        task.setMilliseconds(milliseconds);
    }
}
