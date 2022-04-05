package soya.framework.commons.util;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Enumeration;
import java.util.Properties;

public class PropertiesUtils {
    private PropertiesUtils() {
    }

    public static void compile(Properties properties) {

        int num = properties.size();
        while(true) {
            Properties values = new Properties();
            properties.entrySet().forEach(e -> {
                String v = (String) e.getValue();
                if (!v.contains("${")) {
                    values.setProperty((String) e.getKey(), v);
                }
            });

            if(values.size() == num) {
                break;

            } else {
                num = values.size();

            }

            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);

                if (value.contains("${")) {
                    if (value.contains("${" + key + "}")) {
                        throw new IllegalArgumentException("Self referenced for " + "${" + key + "}");
                    }

                    value = StrSubstitutor.replace(value, values);

                    if(value.contains("${")) {
                        value = StrSubstitutor.replace(value, System.getProperties());
                    }

                    properties.setProperty(key, value);

                }
            }
        }
    }


    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("workspace.home.xxx", "C:/github/Workshop/${AppBuild}");
        properties.setProperty("workspace.home", "C:/github/Workshop/AppBuild");
        properties.setProperty("workspace.cmm.dir", "${workspace.home}/CMM");
        properties.setProperty("workspace.avsc.dir", "${workspace.cmm.dir}/avsc");
        properties.setProperty("workspace.bo.dir", "${workspace.home}/BusinessObjects");
        properties.setProperty("workspace.mustache.dir", "${workspace.home}/Templates");
        properties.setProperty("workspace.log.dir", "${workspace.home}/log");

        compile(properties);



        properties.entrySet().forEach(e -> {
            System.out.println("-------- " + e.getKey() + ": " + e.getValue());
        });
    }
}
