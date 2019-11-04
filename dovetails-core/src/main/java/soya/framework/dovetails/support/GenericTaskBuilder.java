package soya.framework.dovetails.support;

import com.google.gson.JsonElement;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.Task;

public abstract class GenericTaskBuilder<T extends Task> extends TaskBuilderSupport<T> {
    protected transient JsonElement taskDefinition;

    @Override
    protected void configure(T task, ProcessContext context) throws Exception {
        configure(task, taskDefinition, context);
    }

    protected abstract void configure(T task, JsonElement taskDefinition, ProcessContext context) throws Exception;
}
