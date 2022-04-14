package soya.framework.commands.quartz;

import org.quartz.impl.matchers.GroupMatcher;
import soya.framework.core.CommandOption;

public abstract class MatcherCommand extends QuartzSchedulerCommand {

    @CommandOption(option = "q")
    protected String matcher;

    protected GroupMatcher groupMatcher() {
        String token = matcher;

        return GroupMatcher.anyTriggerGroup();
    }
}
