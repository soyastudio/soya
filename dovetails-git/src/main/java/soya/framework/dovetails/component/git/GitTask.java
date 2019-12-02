package soya.framework.dovetails.component.git;


import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

public class GitTask extends Task implements ProcessContextAware {
    private ProcessContext context;

    GitCmd command;

    protected GitTask(String uri) {
        super(uri);
    }

    @Override
    public void setProcessContext(ProcessContext context) {
        this.context = context;
    }

    @Override
    public void process(TaskSession session) throws Exception {
        command.create(session).call();
    }
}
