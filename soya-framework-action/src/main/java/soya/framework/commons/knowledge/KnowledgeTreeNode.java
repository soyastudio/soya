package soya.framework.commons.knowledge;

import java.util.List;

public interface KnowledgeTreeNode<T> extends TreeNode, KnowledgeNode<T> {

    KnowledgeTreeNode getParent();

    List<KnowledgeTreeNode> getChildren();

    KnowledgeNode<T> getData();

}
