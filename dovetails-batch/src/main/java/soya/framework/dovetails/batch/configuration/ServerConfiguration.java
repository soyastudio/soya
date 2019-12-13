package soya.framework.dovetails.batch.configuration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.ByteStreams;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import soya.framework.dovetails.batch.service.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Properties;

@Configuration
public class ServerConfiguration {
    private static final String SERVER_CONFIGURATION_PREFIX = "soya.framework.dovetails.server.";

    @Autowired
    private Environment environment;

    private File home;
    private File conf;
    private Properties configuration;

    @PostConstruct
    public void init()  throws IOException{
        home = new File(environment.getProperty(SERVER_CONFIGURATION_PREFIX + "home"));
        if (!home.exists()) {
            home.mkdirs();
        }
        conf = new File(home, "conf");
        if(!conf.exists()) {
            conf.mkdir();
        }

        File configFile = new File(environment.getProperty(SERVER_CONFIGURATION_PREFIX + "configuration"));
        if(!configFile.exists()) {
            configFile.createNewFile();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties");
            OutputStream os = new FileOutputStream(configFile);
            ByteStreams.copy(is, os);
            os.close();
            is.close();
        }
        Properties configuration = new Properties();
        configuration.load(new FileInputStream(configFile));
    }

    @Bean
    public Server server() {
        return new DefaultServer(home, configuration);
    }

    @Bean
    public PipelineMonitoringService repositoryService() {
        File repository = new File(home, "pipeline");
        if (!repository.exists()) {
            repository.mkdirs();
        }
        return new DefaultPipelineMonitoringService(repository);
    }

    @Bean
    public PipelineService pipelineService() {
        return new DefaultPipelineService();
    }

    @Bean
    public SecurityService securityService() throws IOException {
        File file = new File(conf, SecurityService.SECURITY_KEY_FILE);
        if(!file.exists()) {
            file.createNewFile();
        }
        return new DefaultSecurityService(file);
    }

    static class KeygenJob extends Heartbeat<KeygenEvent> {
        @Override
        protected KeygenEvent nextBeat() {
            return new KeygenEvent();
        }
    }

    static class DeploymentScanJob extends Heartbeat<DeploymentScanEvent> {

        @Override
        protected DeploymentScanEvent nextBeat() {
            return new DeploymentScanEvent();
        }
    }

    static class KeygenEvent extends HeartbeatEvent {
        protected KeygenEvent() {
        }
    }

    static class DeploymentScanEvent extends HeartbeatEvent {
    }

    static class PipelineScanEvent implements ServiceEvent {

    }

    static class PipelineScanJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Server.getInstance().publish(new PipelineScanEvent());
        }
    }

    static class DefaultServer extends Server implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        @Autowired
        private Scheduler scheduler;

        private EventBus eventBus;

        protected DefaultServer(File home, Properties configuration) {
            super();
            this.home = home;
            this.configuration = configuration;
            eventBus = new EventBus();

            instance = this;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @EventListener(classes = {ApplicationReadyEvent.class})
        public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
            try {
                // register service event listeners:
                applicationContext.getBeansOfType(ServiceEventListener.class).values().forEach(e -> {
                    eventBus.register(e);
                });

                // keygen:
                JobDetail keyGenJob = JobBuilder.newJob(KeygenJob.class).withIdentity("keygen", "heartbeat")
                        .build();

                Trigger keygenTrigger = TriggerBuilder.newTrigger().withIdentity("keygen_trigger", "heartbeat")
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * * * ?"))
                        .build();
                scheduler.scheduleJob(keyGenJob, keygenTrigger);

                // scan:
                JobDetail scanJob = JobBuilder.newJob(DeploymentScanJob.class)
                        .withIdentity("scan", "heartbeat")
                        .build();

                Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity("scan_trigger", "heartbeat")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(Integer.parseInt("10"))
                                .repeatForever())
                        .build();

                scheduler.scheduleJob(scanJob, scanTrigger);

                // pipeline scan:
                JobDetail pipelineScanJob = JobBuilder.newJob(PipelineScanJob.class)
                        .withIdentity("pipeline-scan", "heartbeat")
                        .build();

                Trigger pipelineScanTrigger = TriggerBuilder.newTrigger().withIdentity("pipeline_scan_trigger", "heartbeat")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(Integer.parseInt("300"))
                                .repeatForever())
                        .build();

                scheduler.scheduleJob(pipelineScanJob, pipelineScanTrigger);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void publish(ServiceEvent event) {
            eventBus.post(event);
        }
    }

    static class DefaultPipelineMonitoringService extends PipelineMonitoringService implements ServiceEventListener<DeploymentScanEvent> {

        public DefaultPipelineMonitoringService(File home) {
            super(home);
        }

        @Subscribe
        public void onEvent(DeploymentScanEvent event) {
            refresh();
        }
    }

    static class DefaultSecurityService extends SecurityService implements ServiceEventListener<SecurityKeygenEvent> {
        protected DefaultSecurityService(File file) {
            super(file);
        }

        @Subscribe
        public void onEvent(SecurityKeygenEvent event) {
            refresh();
        }
    }

    static class DefaultPipelineService extends PipelineService implements ServiceEventListener<PipelineScanEvent> {

        @Override
        public void onEvent(PipelineScanEvent event) {
            System.out.println("------------------- get event: " + event);
        }
    }
}
