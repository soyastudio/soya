package soya.framework.dovetails.component.context;

import com.google.gson.JsonElement;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.dovetails.support.BeanDescriptor;
import soya.framework.dovetails.support.GenericTaskBuilder;
import soya.framework.dovetails.support.ProcessContextSupport;

@TaskDef(schema = "task")
public class TaskDefTaskBuilder extends GenericTaskBuilder<TaskDefTask> {

    @Override
    protected void configure(TaskDefTask task, JsonElement taskDefinition, ProcessContext context) throws Exception {
        ProcessContextSupport ctx = (ProcessContextSupport) context;
        BeanDescriptor descriptor = new BeanDescriptor(task.getName(), task.getPath(), taskDefinition.getAsJsonObject());

        TaskProcessor processor = BeanDescriptor.newInstance(descriptor, context);
        ctx.setProcessor(descriptor.getName(), processor);

        task.processor = processor;
    }
}
