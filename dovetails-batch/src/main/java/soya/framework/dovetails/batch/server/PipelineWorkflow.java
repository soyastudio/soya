package soya.framework.dovetails.batch.server;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;

public class PipelineWorkflow implements Job {
    private Pipeline pipeline;

    public PipelineWorkflow(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String getName() {
        return pipeline.getName();
    }

    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public void execute(JobExecution jobExecution) {
        ExecutionContext context = jobExecution.getExecutionContext();
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
