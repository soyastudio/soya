package soya.framework.dovetails.component.context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.support.ProcessContextSupport;

import java.util.*;

public final class ContextTask extends Task implements ProcessContextAware {
    private ProcessContextSupport context;

    List<PropertyDescriptor> properties;
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
        Map<String, PropertyDescriptor> propMap = new HashMap<>();
        properties.forEach(e -> {
            propMap.put(e.getName(), e);
        });




        beans.forEach(c -> {
            System.out.println("------------------------ bean: " + c.getName());
        });
    }
}
