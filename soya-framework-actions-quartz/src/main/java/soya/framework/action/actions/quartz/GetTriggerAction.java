package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "trigger-details", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class GetTriggerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        return null;
    }
}
