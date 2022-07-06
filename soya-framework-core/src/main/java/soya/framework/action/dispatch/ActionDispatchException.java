package soya.framework.action.dispatch;

public class ActionDispatchException extends RuntimeException {

    public ActionDispatchException() {
    }

    public ActionDispatchException(String message) {
        super(message);
    }

    public ActionDispatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionDispatchException(Throwable cause) {
        super(cause);
    }
}
