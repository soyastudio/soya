package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import soya.framework.dovetails.*;
import soya.framework.util.GsonUtils;

import java.io.InputStream;
import java.io.Reader;
import java.util.*;

public class DefaultTaskFlowRegistration implements TaskFlowRegistration {

    private final Map<String, TaskFlow> flows;

    private DefaultTaskFlowRegistration(Map<String, TaskFlow> flows) {
        this.flows = flows;
    }

    @Override
    public String[] taskFlows() {
        return flows.keySet().toArray(new String[flows.size()]);
    }

    @Override
    public TaskFlow getTaskFlow(String uri) {
        return flows.get(uri);
    }

    public static TaskFlowRegistrationBuilder builder() {
        return new TaskFlowRegistrationBuilder();
    }

    public static class TaskFlowRegistrationBuilder {
        private TaskTypeRegistration taskTypeRegistration;
        private ProcessContext context;
        private Map<String, TaskFlowBuilder> set = new HashMap<>();

        private TaskFlowRegistrationBuilder() {
        }

        public TaskFlowRegistrationBuilder setTaskTypeRegistration(TaskTypeRegistration taskTypeRegistration) {
            this.taskTypeRegistration = taskTypeRegistration;
            return this;
        }

        public TaskFlowRegistrationBuilder setProcessContext(ProcessContext context) {
            this.context = context;
            return this;
        }

        public TaskFlowRegistrationBuilder load(String yaml) {
            load(GsonUtils.fromYaml(yaml));
            return this;
        }

        public TaskFlowRegistrationBuilder load(InputStream yaml) {
            load(GsonUtils.fromYaml(yaml));
            return this;
        }

        public TaskFlowRegistrationBuilder load(Reader yaml) {
            load(GsonUtils.fromYaml(yaml));
            return this;
        }

        private void load(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()) {
                set.putAll(fromJsonObject(jsonElement.getAsJsonObject()));

            }
        }

        private Map<String, TaskFlowBuilder> fromJsonObject(JsonObject json) {
            Map<String, TaskFlowBuilder> builders = new HashMap<>();

            Set<Map.Entry<String, JsonElement>> set = json.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                String key = entry.getKey();
                DSL dsl = DSL.fromURI(key);
                if (Dovetails.SCHEMA.equals(dsl.getSchema())) {
                    JsonElement value = entry.getValue();
                    TaskFlowBuilder builder = new TaskFlowBuilder(key, value);

                    builders.put(dsl.getPath(), builder);
                }
            }

            return builders;
        }

        public DefaultTaskFlowRegistration create() {
            if (taskTypeRegistration == null) {
                taskTypeRegistration = DefaultTaskFlowController.getInstance();
            }

            if (taskTypeRegistration == null) {
                throw new IllegalStateException("Cannot find TaskTypeRegistration.");
            }

            if (context == null) {
                context = DefaultTaskFlowController.getInstance().getContext();
            }

            Map<String, TaskFlow> flows = new HashMap<>();
            set.entrySet().forEach(e -> {
                flows.put(e.getKey(), e.getValue().create(context, taskTypeRegistration));
            });

            return new DefaultTaskFlowRegistration(flows);
        }
    }

    public static class DefaultTaskFlow implements TaskFlow {
        private final String uri;
        private final ImmutableList<Task> tasks;

        private DefaultTaskFlow(String uri, List<Task> tasks) {
            this.uri = uri;
            this.tasks = ImmutableList.copyOf(tasks);
        }

        @Override
        public String uri() {
            return uri;
        }

        @Override
        public List<Task> tasks() {
            return tasks;
        }
    }

    public static class TaskFlowBuilder {
        private final String uri;
        private final JsonElement value;

        public TaskFlowBuilder(String uri, JsonElement value) {
            this.uri = uri;
            this.value = value;
        }

        public String getUri() {
            return uri;
        }

        public JsonElement getValue() {
            return value;
        }

        public TaskFlow create(ProcessContext context, TaskTypeRegistration registration) {
            List<Task> tasks = new ArrayList<>();
            if (value.isJsonObject()) {
                tasks.add(fromJsonObject(value.getAsJsonObject(), context, registration));

            } else if (value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    tasks.add(fromJsonObject(array.get(i).getAsJsonObject(), context, registration));
                }
            }
            return new DefaultTaskFlow(uri, tasks);
        }

        private Task fromJsonObject(JsonObject jsonObject, ProcessContext context, TaskTypeRegistration registration) {
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                String uri = entry.getKey();
                DSL dsl = DSL.fromURI(uri);
                Class<? extends TaskBuilder> c = registration.getTaskBuilderType(dsl.getSchema());
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
