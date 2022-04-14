package soya.framework.commands.quartz;

import org.quartz.JobExecutionContext;
import soya.framework.core.Command;

import java.util.List;

@Command(group = "quartz-scheduler", name = "current-executing-jobs", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CurrentExecutingJobsCommand extends QuartzSchedulerCommand{
    @Override
    public String call() throws Exception {
        List<JobExecutionContext> jobExecutionContextList = scheduler.getCurrentlyExecutingJobs();
        jobExecutionContextList.forEach(e -> {

        });

        return null;
    }
}
