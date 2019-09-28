package soya.framework.dovetails;

import soya.framework.DataObject;

import java.io.File;

public interface ProcessContext {
    File getBaseDir();

    String getProperty(String propName);

    TaskProcessor getBean(String name);

    DataObject get(String name);

    boolean containsBean(String name);

    void registerBean(String name, Object bean);

    void set(String name, DataObject dataObject);
}
