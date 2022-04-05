package soya.framework.commons.json;

import com.google.gson.*;

import java.util.*;

public class GsonUtils {

    private GsonUtils() {
    }

    public static boolean isValidJson(String string) {
        try {
            new JsonParser().parse(string);
            return true;

        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    public static Object toStruct(JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }

        if (jsonElement.isJsonPrimitive()) {
            return toPrimitive(jsonElement.getAsJsonPrimitive());

        } else if (jsonElement.isJsonObject()) {
            return toMap(jsonElement.getAsJsonObject());

        } else if (jsonElement.isJsonArray()) {
            return toList(jsonElement.getAsJsonArray());
        }

        return null;
    }

    public static Object toPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();

        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();

        } else {
            return primitive.getAsString();

        }
    }

    public static List<?> toList(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            list.add(toStruct(element));
        }

        return list;
    }

    public static Map<String, Object> toMap(JsonObject obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
        set.forEach(ent -> {
            JsonElement jsonElement = ent.getValue();
            Object value = toStruct(jsonElement);

            map.put(ent.getKey(), value);
        });

        return map;
    }
}
