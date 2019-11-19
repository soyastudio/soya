package soya.framework.dovetails;

import soya.framework.DataObject;

import java.io.File;
import java.util.Properties;

public interface ProcessContext {
    File getBaseDir();

    String getProperty(String propName);

    TaskProcessor getProcessor(String name);
}
