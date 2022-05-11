package soya.framework.kt;

public interface KnowledgeBase<K extends KnowledgeNode> {
    Object getSource();

    K getKnowledge();
}
