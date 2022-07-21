package soya.framework.action;

public interface Executable {

    String[] getArgumentNames();

    ActionResult execute(Object[] args) throws Exception;
}
