package soya.framework.dovetails;

import soya.framework.Session;

import java.util.concurrent.Future;

public interface TaskFlowController {

    TaskSession process(TaskFlow flow);

    TaskSession process(TaskFlow flow, Session session);

    Future<TaskSession> submit(TaskFlow flow);

    Future<TaskSession> submit(TaskFlow flow, Session session);
}
