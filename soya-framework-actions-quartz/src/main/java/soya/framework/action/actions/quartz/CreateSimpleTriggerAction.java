package soya.framework.action.actions.quartz;

import soya.framework.action.Command;

@Command(group = "quartz-scheduler", name = "trigger-create-simple", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CreateSimpleTriggerAction extends QuartzSchedulerAction {
    @Override
    public String execute() throws Exception {
        return null;
    }
}
