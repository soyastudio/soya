package soya.framework.dovetails.batch.server;

public class PipelineEvent extends TraceableEvent {
    private final Pipeline pipeline;

    public PipelineEvent(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
}
