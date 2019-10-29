package soya.framework.util;

import java.util.Properties;

public class PropertyEvaluator implements Comparable<PropertyEvaluator> {
    private static final PropertiesParameterResolver resolver = new PropertiesParameterResolver();

    private final String key;
    private ParameterizedText value;

    public PropertyEvaluator(String key, String value) {
        this.key = key;
        this.value = ParameterizedText.create(value);
    }

    public PropertyEvaluator(String key, String value, Properties values) {
        this.key = key;
        this.value = ParameterizedText
                .create(value)
                .evaluate(values, resolver);
    }

    public String getKey() {
        return key;
    }

    public ParameterizedText getValue() {
        return value;
    }

    public void evaluate(Properties properties) {
        this.value = value.evaluate(properties, resolver);
    }

    @Override
    public int compareTo(PropertyEvaluator o) {
        return this.value.getParameters().size() - o.value.getParameters().size();
    }
}
