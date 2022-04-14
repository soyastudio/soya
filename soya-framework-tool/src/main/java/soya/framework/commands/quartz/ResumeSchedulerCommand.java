package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-resume", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class ResumeSchedulerCommand extends QuartzSchedulerCommand {
    @Override
    public String call() throws Exception {
        scheduler.start();
        return null;
    }
}
