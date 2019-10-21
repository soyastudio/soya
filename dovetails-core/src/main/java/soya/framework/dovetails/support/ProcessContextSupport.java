package soya.framework.dovetails.support;

import soya.framework.DataObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskProcessor;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessContextSupport implements ProcessContext {
    protected Properties properties = new Properties();
    protected Map<String, TaskProcessor> beans = new ConcurrentHashMap<>();

    @Override
    public File getBaseDir() {
        return null;
    }

    @Override
    public String getProperty(String propName) {
        return properties.getProperty(propName);
    }

    @Override
    public TaskProcessor getBean(String name) {
        return beans.get(name);
    }

    @Override
    public DataObject get(String name) {
        return null;
    }

    public void setProperty(String propName, String propValue) {
        properties.setProperty(propName, propValue);
    }

    public void setBean(String name, TaskProcessor processor) {
        beans.put(name, processor);
    }
}
