package soya.framework.dovetails;

public class TaskBuildException extends RuntimeException {
    public TaskBuildException() {
    }

    public TaskBuildException(String message) {
        super(message);
    }

    public TaskBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskBuildException(Throwable cause) {
        super(cause);
    }
}
