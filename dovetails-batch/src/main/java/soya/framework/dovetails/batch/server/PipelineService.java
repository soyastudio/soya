package soya.framework.dovetails.batch.server;

import org.quartz.*;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class PipelineService {
    private static PipelineService instance;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Scheduler scheduler;

    protected PipelineService() {
        instance = this;
    }

    public List<String> getJobNames() {
        List<String> list = new ArrayList<>(jobOperator.getJobNames());
        Collections.sort(list);
        return list;
    }

    public void launch(Deployment deployment) {
        if (deployment.deployable() && !jobOperator.getJobNames().contains(deployment.getName())) {
            deployment.setState(Deployment.State.DEPLOYING);
            new Timer().schedule(new PipelineLauncher(deployment), new Random().nextInt(60000));
        }
    }

    private void schedule(Pipeline pipeline) throws SchedulerException {

        JobDetail job = JobBuilder.newJob(PipelineRunner.class).withIdentity(pipeline.getName(), "pipeline")
                .build();
        job.getJobDataMap().put("PIPELINE", pipeline);

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(pipeline.getName(), "pipeline")
                .startAt(new Date(System.currentTimeMillis() + new Random().nextInt(300000)))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt("300"))
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    public long startNext(String jobName) {
        if (jobOperator.getJobNames().contains(jobName)) {
            try {
                long id = jobOperator.startNextInstance(jobName);

                return id;

            } catch (JobInstanceAlreadyCompleteException | NoSuchJobException | JobRestartException | JobParametersInvalidException | JobParametersNotFoundException | JobExecutionAlreadyRunningException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    private JobExecution deploy(Deployment deployment) {
        Pipeline pipeline = deployment.getPipeline();

        try {
            JobFactory factory = new PipelineJobFactory(pipeline);
            jobRegistry.register(factory);
            JobExecution execution = jobLauncher.run(jobRegistry.getJob(factory.getJobName()), pipeline.getJobParameters());
            deployment.setState(Deployment.State.DEPLOYED);

            return execution;

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | DuplicateJobException | NoSuchJobException e) {
            throw new RuntimeException(e);

        }

    }

    static class PipelineLauncher extends TimerTask {
        private Deployment deployment;

        public PipelineLauncher(Deployment deployment) {
            this.deployment = deployment;
        }

        @Override
        public void run() {
            instance.deploy(deployment);
            try {
                instance.schedule(deployment.getPipeline());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class PipelineRunner implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Pipeline pipeline = (Pipeline) jobExecutionContext.getJobDetail().getJobDataMap().get("PIPELINE");
            Server.getInstance().publish(new PipelineEvent(pipeline));
        }
    }

}
