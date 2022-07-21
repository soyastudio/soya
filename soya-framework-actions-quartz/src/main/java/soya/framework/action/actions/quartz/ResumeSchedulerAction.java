package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "scheduler-resume", httpMethod = Command.HttpMethod.PUT, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class ResumeSchedulerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        scheduler.start();
        return null;
    }
}
