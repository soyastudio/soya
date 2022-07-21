package soya.framework.server;

public class PipelineAckEvent extends PipelineEvent {
    public PipelineAckEvent(String pipeline) {
        super(pipeline);
    }
}
