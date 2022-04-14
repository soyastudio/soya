package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "trigger-create-cron", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CreateCronTriggerCommand extends QuartzSchedulerCommand {
    @Override
    public String call() throws Exception {
        return null;
    }
}
