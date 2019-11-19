package soya.framework.dovetails.component.context;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskBuildException;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.dovetails.support.BeanDescriptor;
import soya.framework.dovetails.support.DefaultProcessContext;

public final class TaskDefTask extends ContextBuildTask {
    protected TaskDefTask(String uri) {
        super(uri);
    }

    @Override
    public void build(ProcessContext context) {
        try {
            DefaultProcessContext ctx = (DefaultProcessContext) context;
            BeanDescriptor descriptor = new BeanDescriptor(getName(), getPath(), taskDefinition.getAsJsonObject());
            TaskProcessor processor = BeanDescriptor.newInstance(descriptor, context);
            ctx.setProcessor(descriptor.getName(), processor);
        } catch (Exception e) {
            throw new TaskBuildException(e);
        }
    }
}
