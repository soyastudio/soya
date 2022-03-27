package soya.framework.transform.schema;

import java.util.List;

public interface TreeNode {

    String getName();

    TreeNode getParent();

    List<? extends TreeNode> getChildren();

    String getPath();

    Object getData();

}
