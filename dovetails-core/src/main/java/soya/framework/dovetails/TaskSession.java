package soya.framework.dovetails;

import soya.framework.Session;
import soya.framework.support.SessionSupport;

public final class TaskSession extends SessionSupport {

    private ProcessContext context;
    private Session parentSession;

    public TaskSession(ProcessContext context) {
        super();
        this.context = context;
    }

    public TaskSession(ProcessContext context, Session parentSession) {
        super();
        this.context = context;
        this.parentSession = parentSession;
        updateState(parentSession.getCurrentState());
    }

    public ProcessContext getContext() {
        return context;
    }
}
