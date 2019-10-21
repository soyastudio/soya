package soya.framework.dovetails.support;

import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public abstract class TaskBuilderSupport<T extends Task> implements TaskBuilder<T> {

    public T create(String uri, ProcessContext context) {
        Class<T> clazz = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {String.class});
            constructor.setAccessible(true);

            T task =(T) constructor.newInstance(new Object[] {uri});
            configure(task, context);
            if(task instanceof ProcessContextAware) {
                ((ProcessContextAware) task).setProcessContext(context);
            }

            return task;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void configure(T task, ProcessContext context) {

    }
}
