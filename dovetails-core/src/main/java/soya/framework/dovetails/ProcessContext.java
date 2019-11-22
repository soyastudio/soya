package soya.framework.dovetails;

import soya.framework.DataObject;

import java.io.File;
import java.util.Properties;

public interface ProcessContext {
    ExternalContext getExternalContext();

    String getProperty(String propName);

    TaskProcessor getProcessor(String name);
}
