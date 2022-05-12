package soya.framework.kt;

import java.io.Serializable;

public interface TreePath extends Serializable {

    TreePath getParent();

    String getId();

    int getLevel();



}
