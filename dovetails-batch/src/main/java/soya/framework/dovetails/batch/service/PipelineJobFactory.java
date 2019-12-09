package soya.framework.dovetails.batch.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobFactory;

public class PipelineJobFactory implements JobFactory {
    private Pipeline pipeline;

    public PipelineJobFactory(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String getJobName() {
        return pipeline.getMetadata().getName();
    }

    @Override
    public Job createJob() {
        return new PipelineWorkflow(pipeline);
    }
}
