package soya.framework.dovetails.batch.service;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;

import java.util.UUID;

public class PipelineWorkflow implements Job {
    private Pipeline pipeline;

    public PipelineWorkflow(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String getName() {
        return pipeline.getMetadata().getName();
    }

    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public void execute(JobExecution jobExecution) {
        ExecutionContext context = jobExecution.getExecutionContext();
        String uuid = UUID.randomUUID().toString();
        context.putString("uuid", uuid);

        System.out.println("=============== result: " + uuid);
        jobExecution.setStatus(BatchStatus.COMPLETED);
    }

    @Override
    public JobParametersIncrementer getJobParametersIncrementer() {
        return new RunIdIncrementer();
    }

    @Override
    public JobParametersValidator getJobParametersValidator() {
        return new JobParametersValidator() {
            @Override
            public void validate(JobParameters jobParameters) throws JobParametersInvalidException {

            }
        };
    }
}
