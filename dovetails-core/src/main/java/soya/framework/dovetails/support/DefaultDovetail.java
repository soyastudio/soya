package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.Resource;
import soya.framework.dovetails.*;
import soya.framework.dovetails.component.context.ContextBuilder;
import soya.framework.util.GsonUtils;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DefaultDovetail implements Dovetail {
    private TaskFlowController controller;

    private DefaultProcessContext context;

    private String name;
    private TaskFlow mainFlow;
    private TaskFlowBuilder mainFlowBuilder;

    private ImmutableMap<String, TaskFlowBuilder> taskFlowBuilders;

    public DefaultDovetail(InputStream yaml, DefaultProcessContext context, TaskFlowController controller) {
        this.controller = controller;

        this.context = context;

        Map<String, TaskFlowBuilder> builders = new HashMap<>();
        JsonObject json = GsonUtils.fromYaml(yaml).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> map = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : map) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.startsWith("dovetails:main://")) {
                DSL dsl = DSL.fromURI(key);
                this.mainFlowBuilder = new TaskFlowBuilder(value);
                this.name = dsl.getPath();

            } else {
                builders.put(key, new TaskFlowBuilder(value));
            }
        }
        this.taskFlowBuilders = ImmutableMap.copyOf(builders);

        // init context:
        if (mainFlowBuilder == null) {
            throw new IllegalArgumentException("Cannot determine main flow");
        }

        this.mainFlow = create(mainFlowBuilder, context);
    }

    private TaskFlow create(TaskFlowBuilder builder, ProcessContext context) {
        TaskFlow taskFlow = builder.create(context);
        for(Task task: taskFlow.tasks()) {
            if(task instanceof DovetailAware) {
                ((DovetailAware) task).setDovetail(this);
            }
        }

        return taskFlow;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] flows() {
        return taskFlowBuilders.keySet().toArray(new String[taskFlowBuilders.size()]);
    }

    public TaskFlow createTaskFlow(String name, Properties properties) {
        ProcessContext runtime = context.deepCopy(properties);

        TaskFlowBuilder builder = taskFlowBuilders.get(name);
        TaskFlow flow = create(builder, context);
        return flow;
    }

    @Override
    public TaskSession run() {
        Future<TaskSession> future = controller.submit(mainFlow, context.deepCopy(null));
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
    public TaskSession run(String flowName) {
        ProcessContext runtime = context.deepCopy(null);

        TaskFlowBuilder builder = taskFlowBuilders.get(flowName);
        TaskFlow flow = create(builder, runtime);

        Future<TaskSession> future = controller.submit(flow, runtime);
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
    public TaskSession run(String flowName, Properties properties) {
        return null;
    }

    @Override
    public TaskSession run(Resource resource) {
        JsonElement json = GsonUtils.fromYaml(resource.getAsInputStream());
        TaskFlowBuilder builder = new TaskFlowBuilder(json);
        ProcessContext ctx = context.deepCopy(null);

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
                Task task = fromJsonObject(value.getAsJsonObject(), context);
                if(task instanceof ContextBuilder) {
                    ((ContextBuilder) task).build(context);
                } else {
                    tasks.add(task);
                }
            } else if (value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject jo = array.get(i).getAsJsonObject();
                    Task task = fromJsonObject(jo, context);
                    if(task instanceof ContextBuilder) {
                        ((ContextBuilder) task).build(context);
                    } else {
                        tasks.add(task);
                    }
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

                Task task = taskBuilder.create(uri, context);

                return task;
            }

            throw new IllegalArgumentException("Cannot create task from json: " + jsonObject.toString());
        }
    }

}
