package soya.framework.commandline.tasks.quartz;

import org.quartz.Scheduler;
import soya.framework.commandline.Task;
import soya.framework.commandline.TaskExecutionContext;
import soya.framework.commandline.CommandGroup;

@CommandGroup(group = "quartz-scheduler", title = "Quartz Scheduler", description = "Toolkit for quartz scheduler.")
public abstract class QuartzSchedulerTask extends Task<String> {
    protected Scheduler scheduler;

    public QuartzSchedulerTask() {
        scheduler = TaskExecutionContext.getInstance().getService(Scheduler.class);
    }

}
