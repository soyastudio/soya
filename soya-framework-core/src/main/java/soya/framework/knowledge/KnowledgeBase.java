package soya.framework.knowledge;

public interface KnowledgeBase<K extends KnowledgeNode> {
    Object getSource();

    K getKnowledge();
}
