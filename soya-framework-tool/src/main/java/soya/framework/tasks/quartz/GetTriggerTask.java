package soya.framework.tasks.quartz;

import soya.framework.commandline.Command;

@Command(group = "quartz-scheduler", name = "trigger-details", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class GetTriggerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        return null;
    }
}
