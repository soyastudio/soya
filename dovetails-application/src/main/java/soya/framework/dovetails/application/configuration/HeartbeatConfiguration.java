package soya.framework.dovetails.application.configuration;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.dovetails.application.service.DovetailsService;
import soya.framework.dovetails.support.DefaultDovetail;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.Scanner;

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

    @Bean
    public DirectoryScanner directoryScanner(@Value("${soya.framework.dovetails.application.deploy.dir}") String home) {
        return new DirectoryScanner(home);
    }

    @Bean
    public DefaultDovetailService dovetailService() {
        return new DefaultDovetailService();
    }

    static class DirectoryScanner {
        private File home;

        public DirectoryScanner(String dir) {
            this.home = new File(dir);
            if(!home.exists()) {
                home.mkdirs();
            }
        }
    }

    static class Heartbeat implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            DovetailsService.getInstance().execute();
        }
    }

    static class DefaultDovetailService extends DovetailsService {
        @PostConstruct
        public void init() {
            instance = this;
        }
    }
}
