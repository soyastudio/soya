package soya.framework.dovetails.component.ant.adapters;

import org.apache.tools.ant.Task;
import soya.framework.dovetails.TaskSession;

public abstract class SessionAwareAntTask extends Task {
    protected TaskSession session;

    public void setSession(TaskSession session) {
        this.session = session;
    }
}
