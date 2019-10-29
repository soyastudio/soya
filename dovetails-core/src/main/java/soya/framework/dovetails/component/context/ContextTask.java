package soya.framework.dovetails.component.context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.support.ProcessContextSupport;
import soya.framework.util.PropertiesUtils;

import java.util.*;

public final class ContextTask extends Task implements ProcessContextAware {
    private ProcessContextSupport context;

    Properties properties;
    Set<BeanDescriptor> beans;

    protected ContextTask(String uri) {
        super(uri);
    }

    @Override
    public void setProcessContext(ProcessContext context) {
        this.context = (ProcessContextSupport) context;
    }

    @Override
    public void process(TaskSession session) throws Exception {

        Properties values = new Properties(System.getProperties());
        Properties configuration = PropertiesUtils.evaluate(properties, values);

        Enumeration<?> enumeration = configuration.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = configuration.getProperty(key);
            context.setProperty(key, value);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (BeanDescriptor c : beans) {
            System.out.println("------------------------ bean: " + c.getName());
            System.out.println(gson.toJson(c.getConfiguration()));
        }
    }
}
