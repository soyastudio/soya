package soya.framework.core;

import java.util.concurrent.Callable;

public interface TaskCallable extends Callable<TaskResult> {

    @Override
    TaskResult call() throws Exception;
}
