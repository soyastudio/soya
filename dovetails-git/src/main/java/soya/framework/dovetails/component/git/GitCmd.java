package soya.framework.dovetails.component.git;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.jgit.api.GitCommand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.PropertyDef;
import soya.framework.dovetails.TaskSession;

import java.lang.reflect.Field;

public abstract class GitCmd<T extends GitCommand> {

    public GitCmd() {
    }

    protected void configure(JsonElement settings, ProcessContext context) {
        if(settings == null) {
            return;
        }

        Gson gson = new Gson();
        JsonObject json = settings.getAsJsonObject();

        Class<?> clazz = getClass();
        while(!Object.class.equals(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            for(Field field: fields) {
                if(field.getAnnotation(PropertyDef.class) != null ) {
                    PropertyDef propertyDef = field.getAnnotation(PropertyDef.class);
                    String name = propertyDef.value().isEmpty() ? field.getName() : propertyDef.value();
                    if(json.get(name) != null) {
                        Object value = gson.fromJson(json.get(name), field.getType());
                        field.setAccessible(true);
                        try {
                            field.set(this, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }




    }

    protected abstract T create(TaskSession session);
}
