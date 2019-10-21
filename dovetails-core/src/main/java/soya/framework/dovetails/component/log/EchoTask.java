package soya.framework.dovetails.component.log;

import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.Task;

import java.util.logging.Logger;

public final class EchoTask extends Task {
    private static Logger logger = Logger.getLogger(EchoTask.class.getSimpleName());

    protected EchoTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {

        String name = getName() == null? "Echo: " : getName() + ": ";
        String message = "";
        if(getPath() != null) {
            message = getPath().replaceAll("_", " ").replaceAll("/", " ");
        }

        System.out.println(name + message);
    }
}
