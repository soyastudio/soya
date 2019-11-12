package soya.framework.dovetails.component.context;

import soya.framework.dovetails.*;
import soya.framework.dovetails.support.DefaultDovetail;

import java.util.Properties;

public final class FlowTask extends Task implements DovetailAware {
    private Dovetail dovetail;

    Properties configuration;

    protected FlowTask(String uri) {
        super(uri);
    }

    @Override
    public void setDovetail(Dovetail dovetail) {
        if (this.dovetail == null) {
            this.dovetail = dovetail;
        } else {
            throw new IllegalStateException("Dovetail is already set.");
        }
    }

    @Override
    public void process(TaskSession session) throws Exception {
        TaskFlow flow = dovetail.getTaskFlow(getPath());
        for (Task e : flow.tasks()) {
            e.process(session);
        }
    }
}
