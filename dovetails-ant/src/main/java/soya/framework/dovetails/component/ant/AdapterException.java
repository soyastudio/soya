package soya.framework.dovetails.component.ant;

public class AdapterException extends RuntimeException {
    public AdapterException() {
    }

    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdapterException(Throwable cause) {
        super(cause);
    }
}
