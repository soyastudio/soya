package soya.framework.commons.reflect.descriptor;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TypeDescriptor {
    private transient Class<?> rawType;

    private String dataType;
    private ContainerType containerType;

    private TypeDescriptor(Type type) {
        this.rawType = TypeToken.of(type).getRawType();
        if (rawType.isArray()) {
            this.dataType = rawType.getComponentType().getTypeName();
            this.containerType = ContainerType.ARRAY;

        } else if (Collection.class.isAssignableFrom(rawType)) {
            Type t = ((ParameterizedType) type).getActualTypeArguments()[0];
            this.dataType = t.getTypeName();
            if (List.class.isAssignableFrom(rawType)) {
                this.containerType = ContainerType.LIST;

            } else if (Set.class.isAssignableFrom(rawType)) {
                this.containerType = ContainerType.SET;

            } else {
                this.containerType = ContainerType.LIST;
            }
        } else {
            this.dataType = type.getTypeName();
        }
    }

    public Class<?> getRawType() {
        return rawType;
    }

    public Class<?> getComponentType() {
        try {
            return Class.forName(dataType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isInstance(Object obj) {
        return rawType.isInstance(obj);
    }

    public Object defaultValue() {
        return DefaultValueGeneratorSingleton.getInstance().generate(rawType);
    }

    public String defaulValueAsJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Class<?> type = getComponentType();

        JsonElement jsonElement = toJsonTree(type, gson);
        if (this.containerType != null) {
            JsonArray arr = new JsonArray();
            arr.add(jsonElement);
            return gson.toJson(arr);

        } else {
            return gson.toJson(jsonElement);
        }
    }

    private JsonElement toJsonTree(Class<?> type, Gson gson) {
        if (type == null) {
            return JsonNull.INSTANCE;
        }

        Object o = ArbitraryInstances.get(type);
        JsonElement jsonElement = o == null ? new JsonObject() : gson.toJsonTree(o);

        if (jsonElement.isJsonObject()) {
            JsonObject jo = jsonElement.getAsJsonObject();
            Class<?> clazz = type;
            while (!clazz.equals(Object.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    int ms = field.getModifiers();
                    if (jo.get(field.getName()) == null && !Modifier.isStatic(ms) && !Modifier.isTransient(ms) && !Modifier.isNative(ms)) {
                        Class<?> t = field.getType();
                        JsonArray array = null;
                        JsonElement je;
                        if(t.isArray()) {
                            array = new JsonArray();
                            t = t.getComponentType();

                        } else if(Collection.class.isAssignableFrom(t)){
                            array = new JsonArray();
                            t = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];

                        }

                        if (String.class.getName().equals(t.getTypeName())) {
                            je = new JsonPrimitive(field.getName());

                        } else {
                            je = toJsonTree(t, gson);
                        }

                        if(array == null) {
                            jo.add(field.getName(), je);
                        } else {
                            array.add(je);
                            jo.add(field.getName(), array);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        return jsonElement;
    }

    public static TypeDescriptor fromType(Type type) {
        return new TypeDescriptor(type);
    }

    public static enum ContainerType {
        ARRAY, LIST, SET;
    }

}
