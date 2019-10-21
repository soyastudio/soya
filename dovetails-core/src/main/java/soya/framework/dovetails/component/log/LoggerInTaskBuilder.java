package soya.framework.dovetails.component.log;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

@TaskDef(schema = "logger-in")
public class LoggerInTaskBuilder extends TaskBuilderSupport<LoggerInTask> {
    @Override
    public LoggerInTask create(String uri, ProcessContext context) {
        return new LoggerInTask(uri);
    }
}
