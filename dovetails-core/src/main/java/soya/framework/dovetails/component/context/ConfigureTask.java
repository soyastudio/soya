package soya.framework.dovetails.component.context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.support.DefaultProcessContext;
import soya.framework.util.PropertiesUtils;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public final class ConfigureTask extends ContextBuildTask  {

    protected ConfigureTask(String uri) {
        super(uri);
    }

    @Override
    public void build(ProcessContext context) {
        DefaultProcessContext ctx = (DefaultProcessContext) context;
        Properties values = new Properties(System.getProperties());

        Properties configuration = PropertiesUtils.evaluate(toProperties(taskDefinition), values);
        Enumeration<?> enumeration = configuration.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = configuration.getProperty(key);
            ctx.setProperty(key, value);
        }
    }

    private Properties toProperties(JsonElement json) {
        Properties properties = new Properties();
        if (json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                properties.setProperty(key, value);
            }
        } else if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                o.entrySet().forEach(p -> {
                    String key = p.getKey();
                    String value = p.getValue().getAsString();
                    properties.setProperty(key, value);
                });
            });
        }

        return properties;
    }
}
