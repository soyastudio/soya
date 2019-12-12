package soya.framework.dovetails.batch.service;

import org.quartz.Scheduler;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PipelineService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobOperator jobOperator;

    protected PipelineService() {
    }

    public List<String> getJobNames() {
        List<String> list = new ArrayList<>(jobOperator.getJobNames());
        Collections.sort(list);
        return list;
    }

    public void launch(Deployment deployment) {
        if (!jobOperator.getJobNames().contains(deployment.getName())) {
            deploy(deployment);
        }

    }

    public long startNext(String jobName) {
        if (jobOperator.getJobNames().contains(jobName)) {
            try {
                long id = jobOperator.startNextInstance(jobName);

                return id;

            } catch (JobInstanceAlreadyCompleteException e) {
                e.printStackTrace();
            } catch (NoSuchJobException e) {
                e.printStackTrace();
            } catch (JobRestartException e) {
                e.printStackTrace();
            } catch (JobParametersInvalidException e) {
                e.printStackTrace();
            } catch (JobParametersNotFoundException e) {
                e.printStackTrace();
            } catch (JobExecutionAlreadyRunningException e) {
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
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return execution;

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | DuplicateJobException | NoSuchJobException e) {
            throw new RuntimeException(e);
        }

    }
}
