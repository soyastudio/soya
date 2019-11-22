package soya.framework.dovetails.component.file;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

import java.io.File;

@TaskDef(schema = "mkdir")
public class MkdirTaskBuilder extends TaskBuilderSupport<MkdirTask> {

    private String location;

    @Override
    protected void configure(MkdirTask task, ProcessContext context) {
        if(location == null) {
            task.dir = new File(context.getExternalContext().getBaseDir(), task.getPath());
        }
    }
}
