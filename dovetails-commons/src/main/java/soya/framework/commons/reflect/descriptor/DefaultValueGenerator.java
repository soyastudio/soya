package soya.framework.commons.reflect.descriptor;

public interface DefaultValueGenerator {
    <T> T generate(Class<T> type);
}
