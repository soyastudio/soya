package soya.framework.dovetails.component.context;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.dovetails.TaskSession;

public final class TaskDefTask extends Task {
    TaskProcessor processor;

    protected TaskDefTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {

    }
}
