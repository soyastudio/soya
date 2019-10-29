package soya.framework.util;

import java.util.Properties;

public class PropertiesParameterResolver implements ParameterResolver<Properties> {
    @Override
    public String evaluate(String parameter, Properties properties) {
        return properties.getProperty(parameter);
    }
}
