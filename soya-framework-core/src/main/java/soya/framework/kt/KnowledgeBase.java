package soya.framework.kt;

public interface KnowledgeBase<T, K extends Annotatable> {
    T tao();

    K knowledge();
}
