package soya.framework.dovetails;

import soya.framework.DataObject;

import java.io.File;

public interface ProcessContext {
    File getBaseDir();

    String getProperty(String propName);

    TaskProcessor getProcessor(String name);

    DataObject get(String name);
}
