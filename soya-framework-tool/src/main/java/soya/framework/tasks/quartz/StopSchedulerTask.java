package soya.framework.tasks.quartz;

import soya.framework.commandline.Command;

@Command(group = "quartz-scheduler", name = "scheduler-stop", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class StopSchedulerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        scheduler.shutdown();
        return null;
    }
}
