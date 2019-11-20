package soya.framework.dovetails;

import java.io.File;

public interface ExternalContext {
    File getBaseDir();

    <T> T getResource(String name, Class<T> type);

    String getProperty(String propName);

}
