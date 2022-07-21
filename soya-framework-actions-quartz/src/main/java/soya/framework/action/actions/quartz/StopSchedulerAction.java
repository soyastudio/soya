package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "scheduler-stop", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class StopSchedulerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        scheduler.shutdown();
        return null;
    }
}
