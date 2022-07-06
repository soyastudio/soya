package soya.framework.action;

public interface TaskFlow {

    interface Session {

    }

    interface TaskNode {
        String name();

        String startPoint();

        ActionName taskName();

        TaskBuilder taskBuilder();

        TaskResultHandler resultHandler();

    }

    interface TaskBuilder {
        void builder(ActionCallable task, Session session);

    }

    interface TaskResultHandler {
        void process(ActionResult result, Session session);
    }
}
