package soya.framework.dovetails.component.log;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

@TaskDef(schema = "logger-out")
public class LoggerOutTaskBuilder extends TaskBuilderSupport<LoggerOutTask> {

    @Override
    public LoggerOutTask create(String uri, ProcessContext context) {
        return null;
    }
}
