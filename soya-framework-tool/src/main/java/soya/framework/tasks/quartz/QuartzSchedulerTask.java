package soya.framework.tasks.quartz;

import org.quartz.Scheduler;
import soya.framework.core.Task;
import soya.framework.core.TaskExecutionContext;
import soya.framework.core.CommandGroup;

@CommandGroup(group = "quartz-scheduler", title = "Quartz Scheduler", description = "Toolkit for quartz scheduler.")
public abstract class QuartzSchedulerTask extends Task<String> {
    protected Scheduler scheduler;

    public QuartzSchedulerTask() {
        scheduler = TaskExecutionContext.getInstance().getService(Scheduler.class);
    }

}
