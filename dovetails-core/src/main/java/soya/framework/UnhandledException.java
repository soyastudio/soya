package soya.framework;

public class UnhandledException extends RuntimeException {
    private final Session session;

    public UnhandledException(Throwable cause, Session session) {
        super(cause);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
