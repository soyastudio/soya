package soya.framework.dovetails.component.log;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

import java.io.File;

@TaskDef(schema = "log")
public final class LogTaskBuilder extends TaskBuilderSupport<LogTask> {

    private String filter;

    @Override
    public LogTask create(String uri, ProcessContext context) {
        LogTask processor = new LogTask(uri);

        String path = processor.getPath();
        if(path != null && !path.isEmpty()) {
            File file = new File(context.getBaseDir(), path);
            processor.setLogFile(file);
        }

        return processor;
    }
}
