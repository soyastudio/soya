package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "trigger-create-cron", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CreateCronTriggerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        return null;
    }
}
