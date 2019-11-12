package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.dovetails.Dovetails;
import soya.framework.dovetails.TaskBuilder;
import soya.framework.dovetails.TaskDef;
import soya.framework.util.ClasspathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TaskBuilders {

    private static ImmutableMap<String, Class<? extends TaskBuilder>> taskBuilderTypes;

    static {
        Map<String, Class<? extends TaskBuilder>> map = new HashMap<>();
        Set<Class<?>> classes = ClasspathUtils.findByAnnotation(TaskDef.class, Dovetails.class.getPackage().getName());
        classes.forEach(e -> {
            TaskDef def = e.getAnnotation(TaskDef.class);
            String[] arr = def.schema();
            for(String schema: arr) {
                map.put(schema, (Class<? extends TaskBuilder>) e);
            }

        });

        taskBuilderTypes = ImmutableMap.copyOf(map);
    }

    public static Class<? extends TaskBuilder> getTaskBuilderType(String schema) {
        return taskBuilderTypes.get(schema);
    }

}
