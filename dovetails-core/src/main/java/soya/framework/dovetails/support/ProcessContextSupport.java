package soya.framework.dovetails.support;

import soya.framework.DataObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskProcessor;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessContextSupport implements ProcessContext {
    private Properties properties = new Properties();
    private Map<String, TaskProcessor> processors = new ConcurrentHashMap<>();

    @Override
    public File getBaseDir() {
        return null;
    }

    @Override
    public String getProperty(String propName) {
        return properties.getProperty(propName);
    }

    public void setProperty(String propName, String propValue) {
        properties.setProperty(propName, propValue);
    }

    @Override
    public TaskProcessor getProcessor(String name) {
        return processors.get(name);
    }

    public void setProcessor(String name, TaskProcessor processor) {
        if(processor == null) {
            throw new IllegalArgumentException("processor is null.");
        }

        processors.put(name, processor);
    }

    @Override
    public DataObject get(String name) {
        return null;
    }
}
