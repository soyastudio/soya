package soya.framework.kt;

public final class TreePath implements Comparable<TreePath> {

    private TreePath parent;
    private String name;
    private int depth;



    public TreePath(TreePath parent, String name) {
        this.parent = parent;
        this.name = name;
        this.depth = parent.depth + 1;
    }

    @Override
    public int compareTo(TreePath o) {
        return 0;
    }



}
