package soya.framework.dovetails.batch.service;

public class TransformerException extends RuntimeException {
    public TransformerException() {
    }

    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerException(Throwable cause) {
        super(cause);
    }
}
