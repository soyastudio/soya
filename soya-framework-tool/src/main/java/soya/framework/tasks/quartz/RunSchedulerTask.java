package soya.framework.tasks.quartz;

import soya.framework.commandline.Command;

@Command(group = "quartz-scheduler", name = "scheduler-run", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class RunSchedulerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        scheduler.start();
        return null;
    }
}
