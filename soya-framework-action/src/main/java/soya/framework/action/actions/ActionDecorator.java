package soya.framework.action.actions;

import soya.framework.action.ActionCallable;
import soya.framework.action.ActionResult;

import java.lang.reflect.ParameterizedType;

public abstract class ActionDecorator<T extends ActionCallable> implements ActionCallable {

    private T task;

    public ActionDecorator() {
        try {
            Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            this.task = type.newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public ActionResult call() throws Exception {
        decorate(task);
        return task.call();
    }

    protected abstract void decorate(T task);

}
