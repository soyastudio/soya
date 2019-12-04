package soya.framework.dovetails.component.git;

import soya.framework.dovetails.component.git.command.AddCmd;
import soya.framework.dovetails.component.git.command.CheckoutCmd;
import soya.framework.dovetails.component.git.command.CloneCmd;

public enum GitCmdType {
    CLONE("clone", CloneCmd.class),
    CHECKOUT("checkout", CheckoutCmd.class),
    INIT("init", GitCmd.class),
    ADD("add", AddCmd.class),
    REMOVE("remove", GitCmd.class),
    COMMIT("clone", GitCmd.class),
    COMMIT_ALL("clone", GitCmd.class),
    CREATE_BRANCH("clone", GitCmd.class),
    DELETE_BRANCH("clone", GitCmd.class),
    CREATE_TAG("clone", GitCmd.class),
    DELETE_TAG("clone", GitCmd.class),
    STATUS("clone", GitCmd.class),
    LOG("clone", GitCmd.class),
    PUSH("clone", GitCmd.class),
    PUSH_TAG("clone", GitCmd.class),
    PULL("clone", GitCmd.class),
    MERGE("clone", GitCmd.class),
    SHOW_BRANCHES("clone", GitCmd.class),
    SHOW_TAGS("clone", GitCmd.class),
    CHERRYPICK("clone", GitCmd.class),
    REMOTE_ADD("clone", GitCmd.class),
    REMOTE_LIST("clone", GitCmd.class),
    CLEAN("clone", GitCmd.class),
    GC("clone", GitCmd.class);

    private final String name;
    private final Class<? extends GitCmd> type;

    GitCmdType(String name, Class<? extends GitCmd> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<? extends GitCmd> getType() {
        return type;
    }
}
