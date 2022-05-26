package soya.framework.commandline.tasks;

import soya.framework.commandline.TaskCallable;
import soya.framework.commandline.TaskResult;

import java.lang.reflect.ParameterizedType;

public abstract class TaskDecorator<T extends TaskCallable> implements TaskCallable {

    private T task;

    public TaskDecorator() {
        try {
            Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            this.task = type.newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public TaskResult call() throws Exception {
        decorate(task);
        return task.call();
    }

    protected abstract void decorate(T task);

}
