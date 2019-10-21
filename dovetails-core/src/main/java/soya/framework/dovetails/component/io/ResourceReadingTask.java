package soya.framework.dovetails.component.io;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

public class ResourceReadingTask extends Task {
    protected ResourceReadingTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {

    }
}
