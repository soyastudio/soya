package soya.framework.dovetails.support;

import soya.framework.dovetails.ExternalContext;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskProcessor;

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


    public Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }

    @Override
    public ExternalContext getExternalContext() {
        return externalContext;
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

    public ProcessContext deepCopy(Properties configProperties) {
        DefaultProcessContext context = newInstance();
        context.properties = new Properties(properties);
        if(configProperties != null) {
            Enumeration<?> enumeration = configProperties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = configProperties.getProperty(key);
                context.properties.setProperty(key, value);
            }
        }

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
