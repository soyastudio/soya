package soya.framework.commands.quartz;

import soya.framework.core.Command;

@Command(group = "quartz-scheduler", name = "trigger-details", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class GetTriggerCommand extends QuartzSchedulerCommand {
    @Override
    public String call() throws Exception {
        return null;
    }
}
