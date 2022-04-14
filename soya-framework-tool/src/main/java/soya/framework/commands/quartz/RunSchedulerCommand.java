package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-run", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class RunSchedulerCommand extends QuartzSchedulerCommand {
    @Override
    public String call() throws Exception {
        scheduler.start();
        return null;
    }
}
