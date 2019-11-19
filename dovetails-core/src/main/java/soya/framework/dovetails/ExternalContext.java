package soya.framework.dovetails;

public interface ExternalContext {
    <T> T getResource(String name, Class<T> type);

    String getProperty(String propName);

}
