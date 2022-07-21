package soya.framework.server;

public abstract class PipelineEvent extends ServiceEvent {
    private final String pipeline;

    public PipelineEvent(String pipeline) {
        super();
        this.pipeline = pipeline;
    }

    public String getPipeline() {
        return pipeline;
    }
}
