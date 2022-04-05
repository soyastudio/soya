package soya.framework.transform.schema;

public interface KnowledgeBase<T, K extends Annotatable> {
    T tao();

    K knowledge();
}
