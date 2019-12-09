package soya.framework.dovetails.batch.configuration;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.dovetails.batch.service.PipelineService;
import soya.framework.dovetails.batch.service.RepositoryService;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class HeartbeatConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private Scheduler scheduler;

    @Value("${soya.framework.dovetails.application.repository.home}")
    private String repositoryHome;

    @Value("${soya.framework.dovetails.application.heartbeat.scan}")
    private String scanSchedule;

    @Value("${soya.framework.dovetails.application.heartbeat.keygen}")
    private String keygenSchedule;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        // scan:
        JobDetail scanJob = JobBuilder.newJob(Heartbeat.class)
                .withIdentity("scan", "heartbeat")
                .build();

        Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity("scan_trigger", "heartbeat")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(scanSchedule))
                        .repeatForever())
                .build();

        scheduler.scheduleJob(scanJob, scanTrigger);

        // keygen:
        JobDetail keyGenJob = JobBuilder.newJob(KeyGen.class).withIdentity("keygen", "heartbeat")
                .build();

        Trigger keygenTrigger = TriggerBuilder.newTrigger().withIdentity("keygen_trigger", "heartbeat")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(keygenSchedule))
                .build();
        scheduler.scheduleJob(keyGenJob, keygenTrigger);
    }

    @Bean
    public RepositoryService repositoryService() {
        File home = new File(repositoryHome);
        return new DefaultRepositoryService(home);
    }

    @Bean
    public PipelineService dovetailService() {
        return new DefaultPipelineService();
    }

    static class KeyGen implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("----------------- keygen...");
        }
    }

    static class Heartbeat implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            RepositoryService.getInstance().refresh();
        }
    }

    static class DefaultRepositoryService extends RepositoryService {

        public DefaultRepositoryService(File home) {
            super(home);
            instance = this;
        }
    }

    static class DefaultPipelineService extends PipelineService {
        @PostConstruct
        public void init() {
            instance = this;
        }
    }
}
