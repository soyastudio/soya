package soya.framework.dovetails.component.log;

import soya.framework.DataObject;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

import java.util.logging.Logger;

public final class DebugTask extends Task {

    private static Logger logger = Logger.getLogger("DEBUGGER");

    protected DebugTask(String uri) {
        super(uri);
    }

    public void process(TaskSession session) throws Exception {
        DataObject state = session.getCurrentState();
        if (getName() != null && getName().trim().length() > 0) {
            state = (DataObject) session.get(getName());
        }

        if (state != null) {
            logger.info(state.getAsString());
        }

    }
}
