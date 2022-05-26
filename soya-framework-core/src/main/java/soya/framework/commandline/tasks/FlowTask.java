package soya.framework.commandline.tasks;

import soya.framework.commandline.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlowTask implements TaskFlow, TaskCallable {
    protected Map<String, TaskNode> nodes = new LinkedHashMap<>();

    @Override
    public TaskResult call() throws Exception {
        return null;

    }

    private TaskResult process(TaskFlow.TaskNode node, TaskFlow.Session session) throws Exception {
        TaskCallable task = TaskExecutionContext.getInstance().getTaskType(node.taskName()).newInstance();
        if(node.taskBuilder() != null) {
            node.taskBuilder().builder(task, session);
        }

        TaskResult taskResult = TaskRunner.execute(task);

        if(node.resultHandler() != null) {
            node.resultHandler().process(taskResult, session);
        }

        return taskResult;
    }


}
