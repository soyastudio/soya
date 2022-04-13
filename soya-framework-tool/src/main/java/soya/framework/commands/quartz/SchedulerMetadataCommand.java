package soya.framework.commands.quartz;

import org.quartz.Scheduler;
import soya.framework.core.Command;
import soya.framework.core.CommandExecutionContext;

@Command(group = "quartz-scheduler", name = "metadata", httpMethod = Command.HttpMethod.GET)
public class SchedulerMetadataCommand extends QuartzSchedulerCommand {

    @Override
    public String call() throws Exception {
        Scheduler scheduler = CommandExecutionContext.getInstance().getService(Scheduler.class);

        System.out.println("============== " + scheduler.getClass().getName());
        return null;
    }
}
