package soya.framework.dovetails.component.log;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

public class LoggerInTask extends Task {

    protected LoggerInTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {
        session.set(LoggingMessages.ATTR_NAME, new LoggingMessages());
    }
}
