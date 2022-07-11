package soya.framework.action;

public interface Executable {
    ActionResult execute(Object[] args) throws Exception;
}
