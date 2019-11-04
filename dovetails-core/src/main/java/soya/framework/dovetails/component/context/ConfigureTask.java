package soya.framework.dovetails.component.context;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

import java.util.Enumeration;
import java.util.Properties;

public final class ConfigureTask extends Task {

    Properties configuration;

    protected ConfigureTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {
        Enumeration<?> enumeration = configuration.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = configuration.getProperty(key);
            System.out.println("configure property: " + key + " = " + value);
        }
    }
}
