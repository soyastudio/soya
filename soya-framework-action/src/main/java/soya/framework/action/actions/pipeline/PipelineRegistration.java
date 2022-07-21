package soya.framework.action.actions.pipeline;

import soya.framework.action.ActionName;
import soya.framework.action.Pipeline;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineRegistration {
    private static PipelineRegistration me;

    private Map<ActionName, Pipeline> pipelines = new ConcurrentHashMap<>();

    static {
        me = new PipelineRegistration();
    }

    private PipelineRegistration() {
    }

    public static PipelineRegistration getInstance() {
        return me;
    }

    public Pipeline get(ActionName actionName) {
        if(!pipelines.containsKey(actionName)) {
            throw new IllegalArgumentException("Pipeline not found: " + actionName);
        }
        return pipelines.get(actionName);
    }

    public Set<ActionName> pipelineNames() {
        return pipelines.keySet();
    }

    void register(Pipeline pipeline) {
        pipelines.put(pipeline.getName(), pipeline);
    }
}
