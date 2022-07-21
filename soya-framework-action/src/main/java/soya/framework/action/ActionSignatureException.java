package soya.framework.action;

public class ActionSignatureException extends RuntimeException {
    public ActionSignatureException() {
    }

    public ActionSignatureException(String message) {
        super(message);
    }

    public ActionSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionSignatureException(Throwable cause) {
        super(cause);
    }
}
