package soya.framework.dovetails.component.ant;

import com.google.gson.JsonElement;
import soya.framework.dovetails.Dovetails;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.TaskBuilderSupport;
import soya.framework.util.ClasspathUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@TaskDef(schema = "ant")
public class AntTaskBuilder extends TaskBuilderSupport<AntTask> {
    private static Map<String, Class<?>> adapterTypes;

    static {
        adapterTypes = new HashMap<>();
        Set<Class<?>> classes = ClasspathUtils.findByAnnotation(AntTaskDef.class, Dovetails.class.getPackage().getName());
        classes.forEach(c -> {
            AntTaskDef annotation = c.getAnnotation(AntTaskDef.class);
            adapterTypes.put(annotation.name(), c);
        });
    }

    private JsonElement attributes;

    @Override
    protected void configure(AntTask task, ProcessContext context) {
        task.adapter = createAdapter(task.getName(), attributes, context);

    }

    private AntTaskAdapter createAdapter(String name, JsonElement attributes, ProcessContext context) {
        Class<?> type = adapterTypes.get(name);
        try {
            return (AntTaskAdapter) type.getConstructor(JsonElement.class, ProcessContext.class).newInstance(new Object[]{attributes, context});

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new AdapterException(e);
        }
    }
}
