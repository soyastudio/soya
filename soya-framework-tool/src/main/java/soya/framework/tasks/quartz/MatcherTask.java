package soya.framework.tasks.quartz;

import org.quartz.impl.matchers.GroupMatcher;
import soya.framework.core.CommandOption;

public abstract class MatcherTask extends QuartzSchedulerTask {

    @CommandOption(option = "q")
    protected String matcher;

    protected GroupMatcher groupMatcher() {
        String token = matcher;

        return GroupMatcher.anyTriggerGroup();
    }
}
