package soya.framework.commons.knowledge;

public interface KnowledgeNode<T> {

    T origin();

    void annotate(String namespace, Object annotation);

    Object getAnnotation(String namespace);

    <A> A getAnnotation(String namespace, Class<A> annotationType);
}
