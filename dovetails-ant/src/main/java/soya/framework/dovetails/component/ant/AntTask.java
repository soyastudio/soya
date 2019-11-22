package soya.framework.dovetails.component.ant;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

public class AntTask extends Task {

    AntTaskAdapter adapter;

    protected AntTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {
        adapter.preExecute(session);
        adapter.execute();
        adapter.postExecute(session);
    }
}
