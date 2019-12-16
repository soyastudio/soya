package soya.framework.dovetails.batch.server;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class HeartbeatJob<T extends HeartbeatEvent> implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Server.getInstance().publish(nextBeat());
    }

    protected abstract T nextBeat();
}
