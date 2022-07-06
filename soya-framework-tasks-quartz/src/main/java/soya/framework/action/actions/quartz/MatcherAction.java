package soya.framework.action.actions.quartz;

import org.quartz.impl.matchers.GroupMatcher;
import soya.framework.action.CommandOption;

public abstract class MatcherAction extends QuartzSchedulerAction {

    @CommandOption(option = "q")
    protected String matcher;

    protected GroupMatcher groupMatcher() {
        String token = matcher;

        return GroupMatcher.anyTriggerGroup();
    }
}
