package soya.framework.dovetails.component.context;

import com.google.gson.JsonElement;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.support.GenericTaskBuilder;

public abstract class ContextBuildTaskBuilder<T extends ContextBuildTask> extends GenericTaskBuilder<T> {
    @Override
    protected void configure(T task, JsonElement taskDefinition, ProcessContext context) throws Exception {
        task.taskDefinition = taskDefinition;
    }
}
