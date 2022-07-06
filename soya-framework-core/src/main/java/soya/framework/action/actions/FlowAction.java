package soya.framework.action.actions;

import soya.framework.action.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlowAction implements TaskFlow, ActionCallable {
    protected Map<String, TaskNode> nodes = new LinkedHashMap<>();

    @Override
    public ActionResult call() throws Exception {
        return null;

    }

    private ActionResult process(TaskFlow.TaskNode node, TaskFlow.Session session) throws Exception {
        ActionCallable task = ActionContext.getInstance().getActionType(node.taskName()).newInstance();
        if(node.taskBuilder() != null) {
            node.taskBuilder().builder(task, session);
        }

        ActionResult actionResult = TaskRunner.execute(task);

        if(node.resultHandler() != null) {
            node.resultHandler().process(actionResult, session);
        }

        return actionResult;
    }


}
