package soya.framework.action.actions.quartz;

import org.quartz.Scheduler;
import soya.framework.action.Action;
import soya.framework.action.ActionContext;
import soya.framework.action.CommandGroup;

@CommandGroup(group = "quartz-scheduler", title = "Quartz Scheduler", description = "Toolkit for quartz scheduler.")
public abstract class QuartzSchedulerAction extends Action<String> {
    protected Scheduler scheduler;

    public QuartzSchedulerAction() {
        scheduler = ActionContext.getInstance().getService(Scheduler.class);
    }

}
