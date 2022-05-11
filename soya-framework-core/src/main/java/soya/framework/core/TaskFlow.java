package soya.framework.core;

public interface TaskFlow {

    interface Session {

    }

    interface TaskNode {
        String name();

        String startPoint();

        TaskName taskName();

        TaskBuilder taskBuilder();

        TaskResultHandler resultHandler();

    }

    interface TaskBuilder {
        void builder(TaskCallable task, Session session);

    }

    interface TaskResultHandler {
        void process(TaskResult result, Session session);
    }
}
