package soya.framework.commandline.tasks.quartz;

import org.quartz.JobExecutionContext;
import soya.framework.commandline.Command;

import java.util.List;

@Command(group = "quartz-scheduler", name = "current-executing-jobs", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class CurrentExecutingJobsTask extends QuartzSchedulerTask {
    @Override
    public String execute() throws Exception {
        List<JobExecutionContext> jobExecutionContextList = scheduler.getCurrentlyExecutingJobs();
        jobExecutionContextList.forEach(e -> {

        });

        return null;
    }
}
