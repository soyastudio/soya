package soya.framework.tasks.quartz;

import soya.framework.commandline.Command;

@Command(group = "quartz-scheduler", name = "scheduler-pause", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class PauseSchedulerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        System.out.println("================= !!!");

        scheduler.standby();

        return null;
    }
}
