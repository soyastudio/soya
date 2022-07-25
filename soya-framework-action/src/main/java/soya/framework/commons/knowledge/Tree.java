package soya.framework.commons.knowledge;

import java.util.Iterator;

public interface Tree<N extends TreeNode> {
    N root();

    N create(N parent, Object data);

    N create(N parent, String name, Object data);

    boolean contains(String path);

    N get(String path);

    Iterator<String> paths();

    Iterator<N> nodes();
}
