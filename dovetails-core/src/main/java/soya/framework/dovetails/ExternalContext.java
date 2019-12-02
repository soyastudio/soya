package soya.framework.dovetails;

import java.io.File;

public interface ExternalContext {
    File getBaseDir();

    String getProperty(String propName);

    <T> T getService(Class<T> type);

    <T> T getService(String name, Class<T> type);

}
