package soya.framework.dovetails.application.configuration;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;

@Configuration
public class HeartbeatConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(Heartbeat.class)
                .withIdentity("heartbeat", "heartbeat")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    class Heartbeat implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("--------------------- " + new Date());
        }
    }
}
