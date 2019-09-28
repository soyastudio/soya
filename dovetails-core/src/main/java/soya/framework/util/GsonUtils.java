package soya.framework.util;

import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.yaml.snakeyaml.Yaml;
import soya.framework.DataObject;
import soya.framework.JsonCompatible;
import soya.framework.support.GsonCompatible;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
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

    //
    public static String toJson(DataObject o) {
        if (o instanceof JsonCompatible) {
            return ((JsonCompatible) o).getAsJsonString();

        } else {
            String value = o.getAsString();
            JsonParser parser = new JsonParser();
            try {
                // if is json format:
                return parser.parse(value).toString();

            } catch (Exception e) {
                // if is not json format:
                return JsonNull.INSTANCE.getAsString();
            }
        }
    }

    public static JsonElement toJsonElement(Object o) {
        if (o == null) {
            return JsonNull.INSTANCE;
        }

        if (o instanceof JsonElement) {
            return (JsonElement) o;

        } else if (o instanceof GsonCompatible) {
            return ((GsonCompatible) o).getAsJsonElement();

        } else if (o instanceof JsonCompatible) {
            String json = ((JsonCompatible) o).getAsJsonString();
            return new JsonParser().parse(json);

        } else if (o instanceof DataObject) {
            String value = ((DataObject) o).getAsString();
            JsonParser parser = new JsonParser();
            try {
                // if is json format:
                return parser.parse(value);

            } catch (Exception e) {
                // if is not json format:
                return JsonNull.INSTANCE;
            }

        } else if (o instanceof String) {
            return new JsonPrimitive((String) o);

        } else if (o instanceof Number) {
            return new JsonPrimitive((Number) o);

        } else if (o instanceof Boolean) {
            return new JsonPrimitive((Boolean) o);

        } else if (o instanceof Character) {
            return new JsonPrimitive((Character) o);

        } else if (o.getClass().isArray()) {
            JsonArray arr = new JsonArray();
            int len = Array.getLength(o);
            for (int i = 0; i < len; i++) {
                Object e = Array.get(o, i);
                arr.add(toJsonElement(e));
            }

            return arr;
        }

        return new Gson().toJsonTree(o);
    }

    // JsonPath:
    public static JsonElement fromJsonPath(String expression, String json) {
        JsonPath jsonPath = JsonPath.compile(expression);

        return toJsonElement(jsonPath.read(json));
    }

    /**
     * Reads the given jsonPath from this context with
     * <b>SUPPRESS_EXCEPTION Option</b>.
     */
    public static JsonElement readJsonPath(String jsonPath, String json) {
        DocumentContext context = JsonPath
                .using(Configuration.builder()
                        .options(Option.SUPPRESS_EXCEPTIONS)
                        .build())
                .parse(json);

        return toJsonElement(context.read(jsonPath));
    }

    // Common usage:
    public static <T> String toString(final T object) {
        return new Gson().toJson(object);
    }

    public static JsonObject toJsonObject(final String s) {
        return new Gson().fromJson(s, JsonObject.class);
    }

    // yaml:
    public static JsonElement fromYaml(String src) {
        Yaml yaml = new Yaml();
        Object configuration = yaml.load(src);
        return _convertToJson(configuration);
    }

    public static JsonElement fromYaml(InputStream src) {
        Yaml yaml = new Yaml();
        Object configuration = yaml.load(src);
        return _convertToJson(configuration);
    }

    public static JsonElement fromYaml(Reader src) {
        Yaml yaml = new Yaml();
        Object configuration = yaml.load(src);
        return _convertToJson(configuration);
    }

    private static JsonElement _convertToJson(Object o) {
        if (o == null) {
            return JsonNull.INSTANCE;

        } else if (o instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) o;
            JsonObject result = new JsonObject();
            for (Map.Entry<Object, Object> stringObjectEntry : map.entrySet()) {
                String key = stringObjectEntry.getKey().toString();

                result.add(key, _convertToJson(stringObjectEntry.getValue()));
            }

            return result;

        } else if (o instanceof ArrayList) {
            ArrayList arrayList = (ArrayList) o;
            JsonArray result = new JsonArray();
            for (Object arrayObject : arrayList) {
                result.add(_convertToJson(arrayObject));
            }

            return result;

        } else if (o instanceof String) {
            return new JsonPrimitive((String) o);

        } else if (o instanceof Boolean) {
            return new JsonPrimitive((Boolean) o);

        } else if (o instanceof Number) {
            return new JsonPrimitive((Number) o);

        } else {
            return new JsonPrimitive((String) o);
        }
    }

    // Object copy:
    public static <T> T deepCopy(Object obj, Class<T> type) {
        if(obj == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(gson.toJsonTree(obj), type);
    }

    public static <T> T deepCopy(T obj) {
        if(obj == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(gson.toJsonTree(obj), (Type) obj.getClass());
    }
}

