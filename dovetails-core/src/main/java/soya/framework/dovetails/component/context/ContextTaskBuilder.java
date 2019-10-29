package soya.framework.dovetails.component.context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.DSL;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@TaskDef(schema = "context")
public class ContextTaskBuilder extends TaskBuilderSupport<ContextTask> {
    private JsonElement properties;
    private JsonElement beans;

    @Override
    protected void configure(ContextTask task, ProcessContext context) {
        task.properties = toProperties(properties);
        task.beans = toBeans(beans);
    }

    private Properties toProperties(JsonElement json) {
        Properties properties = new Properties();
        if (json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                properties.setProperty(key, value);
            }
        }

        return properties;
    }

    private Set<BeanDescriptor> toBeans(JsonElement json) {
        Set<BeanDescriptor> beans = new HashSet<>();
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            obj.entrySet().forEach(e -> {
                String uri = e.getKey();
                JsonObject jsonObject = e.getValue().getAsJsonObject();
                DSL dsl = DSL.fromURI(uri);
                beans.add(new BeanDescriptor(dsl.getName(), dsl.getPath(), jsonObject));

            });

        } else if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
        }

        return beans;
    }


}
