package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-stop", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class StopSchedulerCommand extends QuartzSchedulerCommand {
    @Override
    public String call() throws Exception {
        scheduler.shutdown();
        return null;
    }
}
