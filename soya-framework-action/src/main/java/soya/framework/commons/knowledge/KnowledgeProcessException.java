package soya.framework.commons.knowledge;

public class KnowledgeProcessException extends Exception {
    public KnowledgeProcessException() {
    }

    public KnowledgeProcessException(String message) {
        super(message);
    }

    public KnowledgeProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public KnowledgeProcessException(Throwable cause) {
        super(cause);
    }
}
