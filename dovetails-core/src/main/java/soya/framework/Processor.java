package soya.framework;

public interface Processor<T extends Session> {
    void process(T session) throws Exception;
}
