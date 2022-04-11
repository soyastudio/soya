package soya.framework.kt;

import java.util.List;

public interface KnowledgeTreeNode<T> extends TreeNode, Annotatable<T> {

    KnowledgeTreeNode getParent();

    List<KnowledgeTreeNode> getChildren();

    Annotatable<T> getData();

}
