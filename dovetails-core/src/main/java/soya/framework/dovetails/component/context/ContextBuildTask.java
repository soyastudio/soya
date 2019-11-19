package soya.framework.dovetails.component.context;

import com.google.gson.JsonElement;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

public abstract class ContextBuildTask extends Task implements ContextBuilder {
    protected JsonElement taskDefinition;

    protected ContextBuildTask(String uri) {
        super(uri);
    }

    public void process(TaskSession session) throws Exception {
    }
}
