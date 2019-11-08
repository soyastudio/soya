package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.dovetails.*;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DefaultDovetail implements Dovetail {
    private static DefaultDovetail me;

    private TaskFlowController controller;
    private TaskFlowRegistration registration;

    private String name;
    private TaskFlow mainFlow;
    private ImmutableMap<String, TaskFlow> flows;

    public DefaultDovetail(TaskFlowController controller, TaskFlowRegistration registration) {
        this.controller = controller;
        this.registration = registration;

        ImmutableMap.Builder<String, TaskFlow> builder = ImmutableMap.<String, TaskFlow>builder();
        for(String f: registration.taskFlows()) {
            if(Dovetails.MAIN_FLOW.equals(f)) {
                this.mainFlow = registration.getTaskFlow(f);
                this.name = DSL.fromURI(mainFlow.uri()).getName();

            } else {
                builder.put(f, registration.getTaskFlow(f));
            }
        }

        this.flows = builder.build();
    }

    public DefaultDovetail(InputStream yamlImput, ProcessContext context) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] flows() {
        return flows.keySet().toArray(new String[flows.size()]);
    }

    @Override
    public TaskSession run() {
        Future<TaskSession> future = controller.submit(mainFlow);
        while (!future.isDone()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            return future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public TaskSession run(String flow) {
        Future<TaskSession> future = controller.submit(registration.getTaskFlow(flow));
        while (!future.isDone()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            return future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);

        }
    }


}
