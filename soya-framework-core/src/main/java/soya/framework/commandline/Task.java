package soya.framework.commandline;

public abstract class Task<T> implements TaskCallable {

    @Override
    public TaskResult call() {
        try {
            init();
            return TaskResult.completed(this, execute());

        } catch (Exception e) {
            return TaskResult.failed(this, e);

        } finally {
            try {
                close();
            } catch (Exception e) {
                return TaskResult.failed(this, e);
            }
        }
    }

    protected void init() throws Exception {

    }

    protected void close() throws Exception {

    }

    protected abstract T execute() throws Exception;

}
