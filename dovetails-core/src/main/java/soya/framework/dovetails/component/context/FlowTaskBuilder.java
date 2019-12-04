package soya.framework.dovetails.component.context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.GenericTaskBuilder;

import java.util.Map;
import java.util.Properties;

@TaskDef(schema = "flow")
public class FlowTaskBuilder extends GenericTaskBuilder<FlowTask> {

    @Override
    protected void configure(FlowTask task, JsonElement taskDefinition, ProcessContext context) throws Exception {

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
