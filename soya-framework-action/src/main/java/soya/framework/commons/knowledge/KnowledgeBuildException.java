package soya.framework.commons.knowledge;

public class KnowledgeBuildException extends Exception {

    public KnowledgeBuildException() {
    }

    public KnowledgeBuildException(String message) {
        super(message);
    }

    public KnowledgeBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public KnowledgeBuildException(Throwable cause) {
        super(cause);
    }
}
