package soya.framework.commons.reflect.descriptor;

public interface DefaultValueGeneratorRegistration {
    void register(Class<?> type, DefaultValueGenerator generator);
}
