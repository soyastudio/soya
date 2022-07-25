package soya.framework.commons.knowledge;

public interface KnowledgeBase<K extends KnowledgeNode> {
    Object getSource();

    K getKnowledge();
}
