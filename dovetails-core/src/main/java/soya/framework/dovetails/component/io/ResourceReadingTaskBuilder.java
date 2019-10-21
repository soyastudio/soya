package soya.framework.dovetails.component.io;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

@TaskDef(schema = "resource")
public class ResourceReadingTaskBuilder extends TaskBuilderSupport<ResourceReadingTask> {
    private String digester;

    @Override
    public ResourceReadingTask create(String uri, ProcessContext context) {
        return new ResourceReadingTask(uri);
    }
}
