package soya.framework.dovetails.batch.configuration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.ByteStreams;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import soya.framework.dovetails.batch.server.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Properties;

@Configuration
public class ServerConfiguration {

    private static final String SPRING_CONFIGURATION_PREFIX = "soya.framework.dovetails.server.";
    private static final String HOME = "home";
    private static final String CONFIGURATION = "configuration";

    // LOCAL DIR:
    private static final String CONF = "conf";
    private static final String GIT_REPOSITORY = "github";
    private static final String PIPELINE = "pipeline";
    private static final String WORKSPACE = "workspace";

    @Autowired
    private Environment environment;

    private String configPrefix;
    private File home;
    private File conf;
    private File github;
    private File workspace;

    private File pipeline;

    private Properties configuration;

    @PostConstruct
    public void init() throws IOException {

        home = new File(environment.getProperty(SPRING_CONFIGURATION_PREFIX + HOME));
        if (!home.exists()) {
            home.mkdirs();
        }
        conf = new File(home, CONF);
        if (!conf.exists()) {
            conf.mkdir();
        }


        github = new File(home, GIT_REPOSITORY);
        if (!github.exists()) {
            github.mkdir();
        }
        workspace = new File(home, WORKSPACE);
        if (!workspace.exists()) {
            workspace.mkdir();
        }

        File configFile = new File(environment.getProperty(SPRING_CONFIGURATION_PREFIX + CONFIGURATION));
        if (!configFile.exists()) {
            configFile.createNewFile();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties");
            OutputStream os = new FileOutputStream(configFile);
            ByteStreams.copy(is, os);
            os.close();
            is.close();
        }

        configuration = new Properties();
        configuration.load(new FileInputStream(configFile));
    }

    @Bean
    public Server server() {
        return new DefaultServer(home, configuration);
    }

    @Bean
    public SecurityService securityService() {
        return new DefaultSecurityService();
    }

    @Bean
    public GithubService githubService() {
        String url = configuration.getProperty("service.github.url");
        return new DefaultGithubService(url, github, workspace);
    }

    @Bean
    public PipelineMonitoringService pipelineMonitoringService() {
        File pipeline = new File(home, PIPELINE);
        if (!pipeline.exists()) {
            pipeline.mkdir();
        }
        return new DefaultPipelineMonitoringService(pipeline);
    }

    @Bean
    public PipelineService pipelineService() {
        return new DefaultPipelineService();
    }

    @Bean
    public PipelineInvokeService pipelineInvokeService(PipelineService pipelineService) {
        return new PipelineInvokeService(pipelineService);
    }

    @Bean
    public EventStoreManager eventStoreManager() {
        return new DefaultEventStoreManager();
    }

    static class DefaultServer extends Server implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        @Autowired
        private Scheduler scheduler;

        @Autowired
        private CaffeineCacheManager caffeineCacheManager;

        private EventBus eventBus;
        private Cache cache;

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
                cache = caffeineCacheManager.getCache("Event");

                // register service event listeners:
                applicationContext.getBeansOfType(ServiceEventListener.class).values().forEach(e -> {
                    eventBus.register(e);
                });

                // keygen:
                String keygenScheduler = configuration.getProperty("service.heartbean.keygen");
                if (keygenScheduler != null) {
                    JobDetail keyGenJob = JobBuilder.newJob(KeyGen.class).withIdentity("keygen", "heartbeat")
                            .build();

                    Trigger keygenTrigger = TriggerBuilder.newTrigger().withIdentity("keygen_trigger", "heartbeat")
                            .startNow()
                            .withSchedule(CronScheduleBuilder.cronSchedule(keygenScheduler))
                            .build();
                    scheduler.scheduleJob(keyGenJob, keygenTrigger);

                }

                // scan:
                String scanScheduler = configuration.getProperty("service.heartbeat.scanner");
                if (scanScheduler != null) {
                    JobDetail scanJob = JobBuilder.newJob(PipelineScanJob.class)
                            .withIdentity("scan", "heartbeat")
                            .build();

                    Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity("scan_trigger", "heartbeat")
                            .startNow()
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(Integer.parseInt(scanScheduler))
                                    .repeatForever())
                            .build();

                    scheduler.scheduleJob(scanJob, scanTrigger);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void publish(ServiceEvent event) {
            eventBus.post(event);
            if (event instanceof TraceableEvent) {
                TraceableEvent traceableEvent = (TraceableEvent) event;
                cache.put(traceableEvent.getId(), traceableEvent);
            }
        }
    }

    static class DefaultGithubService extends GithubService {
        protected DefaultGithubService(String uri, File directory, File workspace) {
            super(uri, directory, workspace);
        }
    }

    static class DefaultPipelineMonitoringService extends PipelineMonitoringService implements ServiceEventListener<PipelineScanEvent> {

        public DefaultPipelineMonitoringService(File home) {
            super(home);
        }

        @Subscribe
        public void onEvent(PipelineScanEvent repositoryScanEvent) {
            refresh();
        }
    }

    static class DefaultPipelineService extends PipelineService implements ServiceEventListener<DeploymentEvent> {
        public DefaultPipelineService() {
            super();
        }

        @Subscribe
        public void onEvent(DeploymentEvent event) {
            Deployment deployment = event.getDeployment();
            if (Deployment.State.NEW.equals(deployment.getState())) {
                launch(deployment);

            } else if (Deployment.State.UPDATED.equals(deployment.getState())) {

            } else if (Deployment.State.REMOVED.equals(deployment.getState())) {
                System.out.println("--------------------- undeploying pipeline: " + event.getDeployment().getName());
            }
        }
    }

    static class PipelineInvokeService implements ServiceEventListener<PipelineEvent> {
        private PipelineService pipelineService;

        protected PipelineInvokeService(PipelineService pipelineService) {
            this.pipelineService = pipelineService;
        }

        @Subscribe
        public void onEvent(PipelineEvent pipelineEvent) {
            pipelineService.startNext(pipelineEvent.getPipeline().getName());
        }
    }

    static class PipelineScanJob extends HeartbeatJob<PipelineScanEvent> {
        @Override
        protected PipelineScanEvent nextBeat() {
            return new PipelineScanEvent();
        }
    }

    static class PipelineScanEvent extends HeartbeatEvent {
        protected PipelineScanEvent() {
        }
    }

    static class DefaultSecurityService extends SecurityService implements ServiceEventListener<KeyGenEvent> {

        @Subscribe
        public void onEvent(KeyGenEvent event) {
            refresh();
        }
    }

    static class KeyGen extends HeartbeatJob<KeyGenEvent> {
        @Override
        protected KeyGenEvent nextBeat() {
            return new KeyGenEvent();
        }
    }

    static class KeyGenEvent extends HeartbeatEvent {
    }
}
