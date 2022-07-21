package soya.framework.action.actions.quartz;

import org.quartz.TriggerKey;
import soya.framework.action.Command;

import java.util.Set;

@Command(group = "quartz-scheduler", name = "trigger-search", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class FindTriggersAction extends MatcherAction {

    @Override
    public String execute() throws Exception {
        Set<TriggerKey> keys = scheduler.getTriggerKeys(groupMatcher());
        System.out.println("=================== " + keys.size());

        scheduler.getTriggerGroupNames().forEach(group -> {

        });

        return null;
    }
}
