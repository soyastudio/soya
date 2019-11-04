package soya.framework.dovetails.component.context;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.support.ProcessContextSupport;

public final class ContextTask extends Task implements ProcessContextAware {
    private ProcessContextSupport context;

    protected ContextTask(String uri) {
        super(uri);
    }

    @Override
    public void setProcessContext(ProcessContext context) {
        this.context = (ProcessContextSupport) context;
    }

    @Override
    public void process(TaskSession session) throws Exception {

    }
}
