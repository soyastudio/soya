package soya.framework.util;

import java.util.*;

public class PropertiesUtils {
    private PropertiesUtils() {
    }

    public static Properties evaluate(Properties src, Properties values) {
        Properties result = new Properties();

        List<PropertyEvaluator> evaluators = createEvaluators(src, values);
        Collections.sort(evaluators);

        boolean changed = true;
        while (changed) {
            changed = false;
            for (PropertyEvaluator e : evaluators) {
                if (result.getProperty(e.getKey()) != null) {
                    // do nothing:
                } else if (!e.getValue().hasParameters()) {
                    result.setProperty(e.getKey(), e.getValue().toString());
                    changed = true;
                } else {
                    e.evaluate(result);
                }
            }
        }

        evaluators.forEach(e -> {
            if(result.getProperty(e.getKey()) == null) {
                result.setProperty(e.getKey(), e.toString());
            }
        });

        return result;
    }

    private static List<PropertyEvaluator> createEvaluators(Properties src, Properties values) {
        List<PropertyEvaluator> list = new ArrayList<>();
        Enumeration<?> enumeration = src.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = src.getProperty(key);
            PropertyEvaluator evaluator = new PropertyEvaluator(key, value, values);
            list.add(evaluator);
        }

        return list;
    }
}
