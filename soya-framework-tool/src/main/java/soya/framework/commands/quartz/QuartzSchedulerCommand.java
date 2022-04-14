package soya.framework.commands.quartz;

import org.quartz.Scheduler;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandExecutionContext;
import soya.framework.core.CommandGroup;

@CommandGroup(group = "quartz-scheduler", title = "Quartz Scheduler", description = "Toolkit for quartz scheduler.")
public abstract class QuartzSchedulerCommand implements CommandCallable<String> {
    protected Scheduler scheduler;

    public QuartzSchedulerCommand() {
        scheduler = CommandExecutionContext.getInstance().getService(Scheduler.class);
    }

}
