package soya.framework.action;

public interface ActionResult {

    ActionName name();

    Object option(String option);

    boolean successful();

    Object result();

}
