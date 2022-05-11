package soya.framework.tasks.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "trigger-create-cron", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CreateCronTriggerTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        return null;
    }
}
