package soya.framework.dovetails.batch.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class Heartbeat<T extends HeartbeatEvent> implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Server.getInstance().publish(nextBeat());
    }

    protected abstract T nextBeat();
}
