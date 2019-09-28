package soya.framework.support;

import com.google.gson.*;
import soya.framework.DataObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;

public final class JsonData implements DataObject, GsonCompatible {

    private static JsonParser jsonParser = new JsonParser();
    private static Gson defaultGson = new Gson();

    protected JsonElement jsonElement;

    private JsonData(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    public JsonElement getAsJsonElement() {
        return jsonElement.deepCopy();
    }

    public String getAsString() {
        return jsonElement.toString();
    }

    @Override
    public String getAsJsonString() {
        return jsonElement.toString();
    }

    @Override
    public String toString() {
        return getAsString();
    }

    public Object toObject(Class<?> type, InstanceCreator<?> creator) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, creator);
        Gson gson  = gsonBuilder.create();

        if(jsonElement.isJsonArray()) {
            JsonArray arr = jsonElement.getAsJsonArray();
            Object results = Array.newInstance(type, arr.size());
            for(int i = 0; i < arr.size(); i ++) {
                JsonElement element = arr.get(i);
                Object o = gson.fromJson(element, type);
                Array.set(results, i, o);
            }

            return results;

        } else {
            return gson.fromJson(jsonElement, type);
        }
    }

    // from json
    public static JsonData fromJson(String json) {
        if(json == null) {
            return new JsonData(JsonNull.INSTANCE);

        } else {
            return new JsonData(jsonParser.parse(json));
        }
    }

    public static JsonData fromeJson(Reader reader) {
        return new JsonData(jsonParser.parse(reader));
    }

    public static JsonData fromJson(InputStream is) {
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(is));
        return fromJson(new Gson().toJson(jsonElement));
    }

    public static JsonData fromJsonElement(JsonElement jsonElement) {
        return new JsonData(jsonElement);
    }

    // from xml
    public static JsonData fromXml(String json) {
        return null;
    }

    public static JsonData fromeXml(Reader reader) {
        return new JsonData(jsonParser.parse(reader));
    }

    public static JsonData fromXml(InputStream is) {
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(is));
        return fromJson(new Gson().toJson(jsonElement));
    }

    // from object
    public static JsonData fromObject(Object obj) {

        if (obj == null) {
            return new JsonData(JsonNull.INSTANCE) ;
        }

        if (obj instanceof JsonData) {
            JsonData src = (JsonData) obj;
            return new JsonData(src.jsonElement.deepCopy());
        }

        return new JsonData(new Gson().toJsonTree(obj));
    }

    public static JsonData fromObject(Object obj, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        if (obj == null) {
            return new JsonData(JsonNull.INSTANCE) ;
        }

        if (obj instanceof JsonData) {
            JsonData src = (JsonData) obj;
            return new JsonData(src.jsonElement.deepCopy());
        }

        return new JsonData(new GsonBuilder().setDateFormat(dateFormat).create().toJsonTree(obj));
    }

    //
    public static <T> T toObject(String json, Class<T> type) {
        JsonData jsonData = new JsonData(jsonParser.parse(json));
        return new Gson().fromJson(jsonData.jsonElement, type);
    }
}
