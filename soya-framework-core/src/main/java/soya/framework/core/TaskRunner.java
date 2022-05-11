package soya.framework.core;

import java.net.URI;
import java.util.concurrent.Future;

public class TaskRunner {

    public static TaskResult execute(URI uri) throws Exception {
        return execute(TaskParser.fromURI(uri));
    }

    public static TaskResult execute(TaskCallable task) throws Exception {
        Future<TaskResult> future = TaskExecutionContext.getInstance().getExecutorService().submit(task);
        while (!future.isDone()) {
            Thread.sleep(100l);
        }

        return future.get();
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("reflect://jmx-mbeans");

            TaskResult result = execute(uri);

            System.out.println(result.toString());

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


    }
}
