package soya.framework.dovetails.component.context;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

import java.util.Properties;

public final class ConfigureTask extends Task  {

    Properties configuration;

    protected ConfigureTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {


    }
}
