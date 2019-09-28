package soya.framework;

public interface ExceptionHandler<E extends Throwable> {
    boolean onException(E e);
}
