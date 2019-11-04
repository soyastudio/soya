package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DefaultDovetail implements Dovetail {
    private String name;
    private TaskFlow mainFlow;
    private ImmutableMap<String, TaskFlow> flows;

    public DefaultDovetail(String name, TaskFlow mainFlow, Map<String, TaskFlow> flows) {
        this.name = name;
        this.mainFlow = mainFlow;
        this.flows = ImmutableMap.copyOf(flows);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run() {
        DefaultTaskFlowController.getInstance().submit(mainFlow);
    }

    @Override
    public void run(String flow) {
        if (flows.containsKey(flow)) {
            DefaultTaskFlowController.getInstance().submit(flows.get(flow));
        }
    }
}
