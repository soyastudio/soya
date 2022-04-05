package soya.framework.commons.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static Gson gson = new Gson();

    public static Map<String, Object> toMap(JsonObject jsonObject) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        jsonObject.entrySet().forEach(e -> {
            String key = e.getKey();
            JsonElement value = e.getValue();
            if (value.isJsonArray()) {
                map.put(key, toList(value.getAsJsonArray()));

            } else if (value.isJsonObject()) {
                map.put(key, toMap(value.getAsJsonObject()));

            } else {
                map.put(key, value.getAsString());
            }

        });

        return map;
    }

    private static List<Object> toList(JsonArray array) {
        List<Object> list = new ArrayList<Object>();
        array.forEach(e -> {
            if (e.isJsonArray()) {
                list.add(toList(e.getAsJsonArray()));

            } else if (e.isJsonObject()) {
                list.add(toMap(e.getAsJsonObject()));

            } else {
                list.add(e.getAsString());
            }

        });

        return list;
    }


    public static JsonObjectBuilder objectBuilder() {
        return new JsonObjectBuilder();
    }

    public static class JsonObjectBuilder {
        private JsonObject object = new JsonObject();

        private JsonObjectBuilder() {
        }

        public JsonObjectBuilder property(String name, Object value) {
            object.add(name, gson.toJsonTree(value));
            return this;
        }

        public JsonObject create() {
            return object;
        }
    }
}
