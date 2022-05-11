package soya.framework.tasks.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-resume", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class ResumeSchedulerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        scheduler.start();
        return null;
    }
}
