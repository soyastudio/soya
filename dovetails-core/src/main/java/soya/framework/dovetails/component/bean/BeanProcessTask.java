package soya.framework.dovetails.component.bean;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.dovetails.TaskSession;

public final class BeanProcessTask extends Task {

    protected TaskProcessor processor;

    protected BeanProcessTask(String uri) {
        super(uri);
    }

    public void process(TaskSession session) throws Exception {
        if(processor == null) {
            throw new IllegalStateException("Process bean is not defined");

        }  else {
            processor.process(session);
        }
    }
}
