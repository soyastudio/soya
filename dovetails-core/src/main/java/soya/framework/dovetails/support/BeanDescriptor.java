package soya.framework.dovetails.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.util.ParameterizedText;

import java.lang.reflect.Field;

public class BeanDescriptor {

    private final String name;
    private final String type;
    private final JsonObject configuration;

    public BeanDescriptor(String name, String type, JsonObject configuration) {
        this.name = name;
        this.type = type;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public JsonObject getConfiguration() {
        return configuration;
    }

    public static TaskProcessor newInstance(BeanDescriptor descriptor, ProcessContext context) throws Exception {

        Class<?> clazz = Class.forName(descriptor.type);
        TaskProcessor bean = (TaskProcessor) clazz.newInstance();
        if (descriptor.configuration != null) {
            JsonObject config = descriptor.configuration;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (descriptor.configuration.get(field.getName()) != null) {
                    field.setAccessible(true);
                    JsonElement jsonElement = config.get(field.getName());

                    field.set(bean, evaluate(jsonElement, field.getType(), context));
                }
            }

        }

        return bean;
    }

    public static TaskProcessor newInstance(TaskProcessor processor, JsonObject configuration, ProcessContext context) throws Exception {
        Gson gson = new Gson();
        TaskProcessor bean = gson.fromJson(gson.toJson(processor), processor.getClass());
        if (configuration != null) {
            Field[] fields = processor.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (configuration.get(field.getName()) != null) {
                    field.setAccessible(true);
                    JsonElement jsonElement = configuration.get(field.getName());

                    field.set(bean, evaluate(jsonElement, field.getType(), context));
                }
            }

        }

        return bean;
    }

    public static TaskProcessor copyOf(TaskProcessor processor) {
        Gson gson = new Gson();
        TaskProcessor bean = gson.fromJson(gson.toJson(processor), processor.getClass());
        return bean;
    }

    private static Object evaluate(JsonElement exp, Class<?> type, ProcessContext context) {
        Object result = null;
        if (exp.isJsonPrimitive() && exp.getAsString().contains("${")) {
            String value = exp.getAsString();
            ParameterizedText pt = ParameterizedText.create(value);
            for (String param : pt.getParameters()) {
                pt = pt.evaluate(param, context.getProperty(param));
            }
            result = pt.toString();

        } else {
            result = new Gson().fromJson(exp, type);

        }

        return result;
    }
}
