package soya.framework.action;

import soya.framework.action.ActionResult;

public interface Executable {
    ActionResult execute(Object[] args) throws Exception;
}
