package soya.framework.dovetails.support;

import soya.framework.dovetails.ExternalContext;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskProcessor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultProcessContext implements ProcessContext {

    private ExternalContext externalContext;
    private Properties properties = new Properties();
    private Map<String, TaskProcessor> processors = new ConcurrentHashMap<>();

    public DefaultProcessContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }

    @Override
    public File getBaseDir() {
        return null;
    }

    public Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }

    @Override
    public String getProperty(String propName) {
        String prop = properties.getProperty(propName);
        if (prop == null) {
            prop = externalContext.getProperty(propName);
        }
        if (prop == null) {
            prop = System.getProperty(propName);
        }
        return prop;
    }

    public void setProperty(String propName, String propValue) {
        properties.setProperty(propName, propValue);
    }

    @Override
    public TaskProcessor getProcessor(String name) {
        return processors.get(name);
    }

    public void setProcessor(String name, TaskProcessor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor is null.");
        }

        processors.put(name, processor);
    }

    @Override
    public ProcessContext deepCopy() {
        DefaultProcessContext context = newInstance();
        context.properties = new Properties(properties);
        context.processors = new ConcurrentHashMap<>();
        processors.entrySet().forEach(e -> {
            String key = e.getKey();
            TaskProcessor processor = BeanDescriptor.copyOf(e.getValue());
            context.processors.put(key, processor);
        });

        return context;
    }

    protected <T extends DefaultProcessContext> T newInstance() {
        try {
            return (T) getClass().getConstructor(ExternalContext.class).newInstance(externalContext);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
