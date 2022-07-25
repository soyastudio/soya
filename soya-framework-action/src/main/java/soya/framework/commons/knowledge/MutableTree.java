package soya.framework.commons.knowledge;

public interface MutableTree<N extends TreeNode> extends Tree<N> {

    void delete(String path);

    void move(String from, String dest);
}
