package soya.framework.commands.quartz;

import org.quartz.TriggerKey;
import soya.framework.core.Command;

import java.util.Set;

@Command(group = "quartz-scheduler", name = "trigger-search", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class FindTriggersCommand extends MatcherCommand {

    @Override
    public String call() throws Exception {
        Set<TriggerKey> keys = scheduler.getTriggerKeys(groupMatcher());
        System.out.println("=================== " + keys.size());

        scheduler.getTriggerGroupNames().forEach(group -> {

        });

        return null;
    }
}
