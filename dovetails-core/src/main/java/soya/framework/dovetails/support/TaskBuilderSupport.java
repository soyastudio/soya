package soya.framework.dovetails.support;

import com.google.gson.JsonElement;
import soya.framework.dovetails.*;
import soya.framework.util.ParameterizedText;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

public abstract class TaskBuilderSupport<T extends Task> implements TaskBuilder<T> {
    public T create(String uri, ProcessContext context) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            Constructor constructor = clazz.getDeclaredConstructor(new Class[]{String.class});
            constructor.setAccessible(true);

            DSL dsl = DSL.fromURI(uri);
            if (dsl.getPath() != null && dsl.getPath().contains("${")) {
                ParameterizedText pt = ParameterizedText.create(dsl.getPath());
                for (String p : pt.getParameters()) {
                    if (context.getProperty(p) == null) {
                        throw new TaskBuildException("Cannot find property '" + p + "'");
                    } else {
                        pt = pt.evaluate(p, context.getProperty(p));
                    }
                }

                dsl = DSL.newInstance(dsl.getSchema(), dsl.getName(), pt.toString());
            }

            T task = (T) constructor.newInstance(new Object[]{dsl.toString()});
            configure(task, context);
            if (task instanceof ProcessContextAware) {
                ((ProcessContextAware) task).setProcessContext(context);
            }

            return task;

        } catch (Exception e) {
            throw new TaskBuildException(e);
        }
    }

    protected void configure(T task, ProcessContext context) throws Exception {

    }
}
