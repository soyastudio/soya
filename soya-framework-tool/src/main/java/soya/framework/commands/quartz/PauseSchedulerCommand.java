package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "scheduler-pause", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class PauseSchedulerCommand extends QuartzSchedulerCommand{
    @Override
    public String call() throws Exception {
        System.out.println("================= !!!");

        scheduler.standby();

        return null;
    }
}
