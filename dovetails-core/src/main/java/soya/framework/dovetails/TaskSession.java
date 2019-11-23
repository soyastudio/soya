package soya.framework.dovetails;

import soya.framework.Session;
import soya.framework.support.SessionSupport;

public final class TaskSession extends SessionSupport {
    public static final String CURRENT_DIRECTORY = "CURRENT_DIRECTORY";

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
