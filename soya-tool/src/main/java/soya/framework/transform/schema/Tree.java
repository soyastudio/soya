package soya.framework.transform.schema;

import java.util.Iterator;
import java.util.Set;

public interface Tree<N extends TreeNode> {
    N root();

    N create(N parent, String name, Object data);

    boolean contains(String path);

    N get(String path);

    Iterator<String> paths();

    Iterator<N> nodes();

    Set<N> find(Selector<N> selector);

    Tree<N> filterIn(Selector<N> selector);

    Tree<N> filterOut(Selector<N> selector);

    interface Selector<N> {
        Set<N> select();
    }
}
