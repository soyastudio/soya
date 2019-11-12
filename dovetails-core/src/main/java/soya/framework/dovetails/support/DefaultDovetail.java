package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.Resource;
import soya.framework.dovetails.*;
import soya.framework.util.GsonUtils;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DefaultDovetail implements Dovetail {
    private ProcessContext context;
    private TaskFlowController controller;

    private String name;
    private TaskFlow mainFlow;
    private ImmutableMap<String, TaskFlow> flows;

    public DefaultDovetail(InputStream yaml, ProcessContext context, TaskFlowController controller) {
        this.context = context;
        this.controller = controller;

        JsonObject jsonObject = GsonUtils.fromYaml(yaml).getAsJsonObject();
        Map<String, TaskFlowBuilder> flowBuilders = fromJsonObject(jsonObject);

        TaskFlowBuilder mainFlowBuilder = null;
        Map<DSL, TaskFlowBuilder> otherFlowBuilders = new HashMap<>();
        for (Map.Entry<String, TaskFlowBuilder> stringTaskFlowBuilderEntry : flowBuilders.entrySet()) {
            String key = stringTaskFlowBuilderEntry.getKey();
            TaskFlowBuilder flowBuilder = stringTaskFlowBuilderEntry.getValue();

            DSL dsl = DSL.fromURI(key);
            if (dsl.isCanonical() && Dovetails.SCHEMA.equals(dsl.getSchema())) {
                if (Dovetails.MAIN_FLOW.equals(dsl.getPath())) {
                    mainFlowBuilder = flowBuilder;
                    this.name = dsl.getName();

                } else {
                    otherFlowBuilders.put(dsl, flowBuilder);
                }
            }

        }

        // init context:
        if (mainFlowBuilder == null) {
            throw new IllegalArgumentException("Cannot determine main flow");
        }
        this.mainFlow = mainFlowBuilder.create(context);

        ImmutableMap.Builder<String, TaskFlow> builder = ImmutableMap.<String, TaskFlow>builder();
        otherFlowBuilders.entrySet().forEach(e -> {
            DSL key = e.getKey();
            TaskFlowBuilder flowBuilder = e.getValue();

            if(name.equals(key.getName())) {
                ProcessContext ctx = context.deepCopy();
                builder.put(key.getPath(), flowBuilder.create(ctx));

            } else {

            }

        });

        this.flows = builder.build();
    }


    private Map<String, TaskFlowBuilder> fromJsonObject(JsonObject json) {
        Map<String, TaskFlowBuilder> builders = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> map = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : map) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            TaskFlowBuilder builder = new TaskFlowBuilder(value);
            builders.put(key, builder);
        }

        return builders;
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
        Future<TaskSession> future = controller.submit(mainFlow, context.deepCopy());
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
        Future<TaskSession> future = controller.submit(flows.get(flow), context.deepCopy());
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
    public TaskSession run(Resource resource) {
        JsonElement json = GsonUtils.fromYaml(resource.getAsInputStream());
        TaskFlowBuilder builder = new TaskFlowBuilder(json);
        ProcessContext ctx = context.deepCopy();

        Future<TaskSession> future = controller.submit(builder.create(ctx), ctx);
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

    public static class DefaultTaskFlow implements TaskFlow {
        private final ImmutableList<Task> tasks;

        private DefaultTaskFlow(List<Task> tasks) {
            this.tasks = ImmutableList.copyOf(tasks);
        }

        @Override
        public List<Task> tasks() {
            return tasks;
        }
    }

    public static class TaskFlowBuilder {
        private final JsonElement value;

        public TaskFlowBuilder(JsonElement value) {
            this.value = value;
        }

        public JsonElement getValue() {
            return value;
        }

        public TaskFlow create(ProcessContext context) {
            List<Task> tasks = new ArrayList<>();
            if (value.isJsonObject()) {
                tasks.add(fromJsonObject(value.getAsJsonObject(), context));

            } else if (value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    tasks.add(fromJsonObject(array.get(i).getAsJsonObject(), context));
                }
            }
            return new DefaultTaskFlow(tasks);
        }

        private Task fromJsonObject(JsonObject jsonObject, ProcessContext context) {
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                String uri = entry.getKey();
                DSL dsl = DSL.fromURI(uri);
                Class<? extends TaskBuilder> c = TaskBuilders.getTaskBuilderType(dsl.getSchema());
                if (c == null) {
                    throw new IllegalArgumentException("Cannot find TaskBuilder from schema: " + dsl.getSchema());
                }

                TaskBuilder taskBuilder = null;
                if (null == entry.getValue() || entry.getValue().isJsonNull()) {
                    try {
                        taskBuilder = c.newInstance();

                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else if (GenericTaskBuilder.class.isAssignableFrom(c)) {
                    try {
                        taskBuilder = c.newInstance();
                        ((GenericTaskBuilder) taskBuilder).taskDefinition = entry.getValue();

                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Gson gson = new Gson();
                    JsonElement jsonElement = entry.getValue();
                    taskBuilder = gson.fromJson(jsonElement, c);
                }

                return taskBuilder.create(uri, context);
            }

            throw new IllegalArgumentException("Cannot create task from json: " + jsonObject.toString());
        }
    }

}
