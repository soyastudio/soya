package soya.framework.dovetails;

import java.util.concurrent.Future;

public interface TaskFlowController {

    TaskSession process(TaskFlow flow, ProcessContext context);

    Future<TaskSession> submit(TaskFlow flow, ProcessContext context);
}
