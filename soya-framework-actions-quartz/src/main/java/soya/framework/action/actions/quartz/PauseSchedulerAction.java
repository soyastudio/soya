package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "scheduler-pause", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class PauseSchedulerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        System.out.println("================= !!!");

        scheduler.standby();

        return null;
    }
}
