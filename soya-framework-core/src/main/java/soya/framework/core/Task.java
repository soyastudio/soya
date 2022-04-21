package soya.framework.core;

public abstract class Task<T> implements CommandCallable<TaskResult> {

    @Override
    public TaskResult call() {
        try {
            return TaskResult.completed(execute());

        } catch (Exception e) {
            return TaskResult.failed(e);
        }
    }

    protected abstract T execute() throws Exception;

}
