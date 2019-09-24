package soya.framework.commons.reflect.descriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultValueGeneratorSingleton implements DefaultValueGenerator, DefaultValueGeneratorRegistration {
    private static DefaultValueGeneratorSingleton instance;

    private Map<Class<?>, DefaultValueGenerator> generators;

    static {
        instance = new DefaultValueGeneratorSingleton();
    }

    private DefaultValueGeneratorSingleton() {
        generators = new ConcurrentHashMap<>();
    }

    public static DefaultValueGeneratorSingleton getInstance() {
        return instance;
    }

    @Override
    public void register(Class<?> type, DefaultValueGenerator generator) {
        generators.put(type, generator);
    }

    @Override
    public <T> T generate(Class<T> type) {
        if (generators.containsKey(type)) {
            return generators.get(type).generate(type);
        } else {
            return getDefaultValue(type);
        }
    }

    private <T> T getDefaultValue(Class<T> type) {
        return ArbitraryInstances.get(type);
    }
}
