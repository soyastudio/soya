package soya.framework.action;

import java.util.concurrent.Callable;

public interface ActionCallable extends Callable<ActionResult> {

    @Override
    ActionResult call() throws Exception;
}
