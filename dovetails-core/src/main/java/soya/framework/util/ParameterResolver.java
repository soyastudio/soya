package soya.framework.util;

public interface ParameterResolver<T> {
    String evaluate(String parameter, T t);
}
