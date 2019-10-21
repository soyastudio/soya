package soya.framework.dovetails.component.bean;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.util.ClasspathUtils;
import soya.framework.dovetails.*;
import soya.framework.dovetails.support.TaskBuilderSupport;

import java.lang.reflect.Type;
import java.util.Set;

@TaskDef(schema = "processor")
public final class BeanProcessTaskBuilder extends TaskBuilderSupport<BeanProcessTask> {
    private static final ImmutableMap<String, Class<? extends TaskProcessor>> predefineds;
    private JsonElement properties;

    static {
        ImmutableMap.Builder<String, Class<? extends TaskProcessor>> builder = ImmutableMap.<String, Class<? extends TaskProcessor>>builder();
        Set<Class<?>> results = ClasspathUtils.findByAnnotation(Predefined.class, BeanProcessTaskBuilder.class.getPackage().getName());
        results.forEach(c -> {
            if(TaskProcessor.class.isAssignableFrom(c)) {
                String name = c.getAnnotation(Predefined.class).value();
                builder.put(name, (Class<? extends TaskProcessor>) c);
            }
        });

        predefineds = builder.build();
    }

    @Override
    protected void configure(BeanProcessTask task, ProcessContext context) {
        if (task.getPath() == null || task.getPath().trim().length() == 0) {
            throw new IllegalArgumentException("Register is not defined.");
        }

        String path = task.getPath();
        TaskProcessor bean;
        if(predefineds.containsKey(path)) {
            if(properties == null) {
                properties = new JsonObject();
            }
            bean = new Gson().fromJson(properties, predefineds.get(path));

            if (bean instanceof ProcessContextAware) {
                ((ProcessContextAware) bean).setProcessContext(context);
            }

        } else if(context.getBean(path) != null) {
            bean = context.getBean(path);

        } else {
            try {
                Class<?> beanClass = Class.forName(path);
                if (properties == null) {
                    bean = (TaskProcessor) beanClass.newInstance();

                } else {
                    bean = new Gson().fromJson(properties, (Type) beanClass);
                }

                if (bean instanceof ProcessContextAware) {
                    ((ProcessContextAware) bean).setProcessContext(context);
                }

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        task.processor = bean;
    }
}
