package soya.framework.tool.ant;

import org.apache.tools.ant.Task;

import java.lang.reflect.ParameterizedType;

public abstract class AntTask<T extends Task> {
    protected T task;

    public AntTask() {
        try {
            Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            task = type.newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void init();

    protected Void invoke() {
        task.execute();
        return null;
    }
}
