package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultTaskFlow implements TaskFlow {
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

    static class TaskFlowBuilder {
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
