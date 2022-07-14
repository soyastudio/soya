package soya.framework.action;

public interface ActionResult {
    ActionName name();

    boolean successful();

    Object result();
}
