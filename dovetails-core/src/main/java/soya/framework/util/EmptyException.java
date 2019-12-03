package soya.framework.util;

public class EmptyException extends RuntimeException {
    public EmptyException() {
    }

    public EmptyException(String name) {
        super("'" + name + "' is null or empty.");
    }
}
