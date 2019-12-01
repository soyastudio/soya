package soya.framework.dovetails;

public interface ServiceManager<T> {
    T start();

    void stop();
}
