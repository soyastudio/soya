package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.dovetails.*;

import java.io.InputStream;

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

        this.name = name;
        this.mainFlow = mainFlow;
        this.flows = ImmutableMap.copyOf(flows);
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
    public void run() {
        controller.submit(mainFlow);
    }

    @Override
    public void run(String flow) {
        if (flows.containsKey(flow)) {
            controller.submit(flows.get(flow));
        }
    }


}
