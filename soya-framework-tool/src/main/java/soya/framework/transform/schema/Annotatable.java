package soya.framework.transform.schema;

public interface Annotatable<T> {

    T origin();

    void annotate(String namespace, Object annotation);

    Object getAnnotation(String namespace);

    <A> A getAnnotation(String namespace, Class<A> annotationType);
}
