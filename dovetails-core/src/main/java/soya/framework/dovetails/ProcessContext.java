package soya.framework.dovetails;

public interface ProcessContext {
    ExternalContext getExternalContext();

    String getProperty(String propName);

    TaskProcessor getProcessor(String name);
}
