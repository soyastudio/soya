package soya.framework.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Pipeline {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final ActionName name;
    private final LinkedHashMap<String, Class<?>> parameterTypes;
    private final List<Task> tasks;
    private String result;

    private Pipeline(ActionName name, LinkedHashMap<String, Class<?>> parameterTypes, List<Task> tasks, String result) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.tasks = tasks;
        this.result = result;
    }

    public ActionName getName() {
        return name;
    }

    public String[] parameterNames() {
        return parameterTypes.keySet().toArray(new String[parameterTypes.size()]);
    }

    public Class<?> parameterType(String name) {
        return parameterTypes.get(name);
    }

    public ActionResult execute(Object[] params) throws Exception {
        Session session = new Session(parameterTypes, params);
        Queue<Task> queue = new ConcurrentLinkedQueue<>(tasks);
        while (!queue.isEmpty()) {
            Task task = queue.poll();
            if (!(new Worker(session, task).execute()) && task.stopOnFailure) {
                break;
            }
        }

        return session.get(result != null ? result : session.cursor);
    }

    public static Pipeline fromJson(String json) {
        PipelineModel model = GSON.fromJson(json, PipelineModel.class);
        Pipeline.Builder builder = Pipeline.builder();
        builder.name(model.id.getGroup() + "://" + model.id.getName());

        model.parameters.forEach(p -> {
            try {
                builder.addParameter(p.name, Class.forName(p.type));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        model.tasks.forEach(t -> {
            Pipeline.TaskBuilder taskBuilder = builder.createTask(t.uri);
            t.options.forEach(o -> {
                taskBuilder.setParameter(o.option, ActionPropertyType.valueOf(o.type), o.expression);
            });

            taskBuilder.add(t.name, t.stopOnFailure);

        });

        builder.setResult(model.result);

        return builder.create();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ActionName actionName;
        private LinkedHashMap<String, Class<?>> parameterTypes = new LinkedHashMap<>();
        private List<Task> tasks = new ArrayList<>();
        private String result;

        private Builder() {
        }

        public Builder name(String uri) {
            this.actionName = ActionName.fromURI(uri);
            return this;
        }

        public Builder addParameter(String name, Class<?> type) {
            parameterTypes.put(name, type);
            return this;
        }

        public TaskBuilder createTask(String commandline) {
            return new TaskBuilder(this, commandline);
        }

        public TaskBuilder createTask(URI uri) {
            return new TaskBuilder(this, uri);
        }

        public Builder setResult(String name) {
            this.result = name;
            return this;
        }

        public Pipeline create() {
            return new Pipeline(actionName, parameterTypes, tasks, result);
        }
    }

    public static class TaskBuilder {
        private Builder builder;
        private ActionSignature.Builder signatureBuilder;

        private TaskBuilder(Builder builder, String commandline) {
            this.builder = builder;
            this.signatureBuilder = ActionSignature.builder(commandline);
        }

        private TaskBuilder(Builder builder, URI uri) {
            this.builder = builder;
            this.signatureBuilder = ActionSignature.builder(uri);
        }

        public TaskBuilder setParameter(String option, ActionPropertyType type, String exp) {
            signatureBuilder.set(option, type, exp);
            return this;
        }

        public Builder add(String name, boolean stopOnFailure) {
            builder.tasks.add(new Task(name, signatureBuilder.create(), stopOnFailure));
            return builder;
        }
    }

    static class Task {
        private final String name;
        private final ActionSignature signature;
        private final boolean stopOnFailure;

        Task(String name, ActionSignature signature, boolean stopOnFailure) {
            this.name = name;
            this.signature = signature;
            this.stopOnFailure = stopOnFailure;
        }
    }

    static class Worker {
        private Session session;
        private Task task;

        Worker(Session session, Task task) {
            this.session = session;
            this.task = task;
        }

        boolean execute() throws Exception {
            session.cursor = task.name;

            ActionClass actionClass = ActionClass.get(task.signature.getActionName());
            Constructor constructor = actionClass.getActionType().getConstructor(new Class[0]);
            constructor.setAccessible(true);
            ActionCallable action = (ActionCallable) constructor.newInstance(new Object[0]);

            Field[] fields = actionClass.getActionFields();
            for (Field field : fields) {
                Object value = null;
                ActionProperty actionProperty = task.signature.getParameter(field.getName());
                ActionPropertyType actionPropertyType = actionProperty.getType();
                if (actionPropertyType.equals(ActionPropertyType.arg)) {
                    value = session.parameterValues.get(actionProperty.getExpression());

                } else if (actionPropertyType.equals(ActionPropertyType.prop)) {
                    value = ActionContext.getInstance().getProperty(actionProperty.getExpression());

                } else if (actionPropertyType.equals(ActionPropertyType.ref)) {
                    value = session.evaluate(actionProperty.getExpression());

                } else if (actionPropertyType.equals(ActionPropertyType.res)) {
                    Resource resource = Resource.create(actionProperty.getExpression());

                } else if (actionPropertyType.equals(ActionPropertyType.val)) {
                    value = actionProperty.getExpression();

                }

                if (value != null) {
                    field.setAccessible(true);
                    field.set(action, value);
                }

            }

            ActionResult result = action.call();
            session.results.put(task.name, result);

            return result.successful();

        }
    }

    static class Session {
        private LinkedHashMap<String, Object> parameterValues;
        private Map<String, ActionResult> results = new LinkedHashMap<>();

        private String cursor;

        Session(LinkedHashMap<String, Class<?>> parameterTypes, Object[] params) {
            if (parameterTypes.size() != params.length) {
                throw new IllegalArgumentException("TODO");
            }

            this.parameterValues = new LinkedHashMap<>();

            String[] paramNames = parameterTypes.keySet().toArray(new String[parameterTypes.size()]);
            for (int i = 0; i < paramNames.length; i++) {
                parameterValues.put(paramNames[i], params[i]);
            }
        }

        public String task() {
            return cursor;
        }

        public ActionResult get(String name) {
            return results.get(name);
        }

        Object evaluate(String expression) {
            try {
                return results.get(expression).result();

            } catch (Exception e) {
                return null;
            }
        }

    }

    static class PipelineModel {
        ActionName id;
        List<ParameterModel> parameters = new ArrayList<>();
        List<TaskModel> tasks = new ArrayList<>();
        String result;
    }

    static class ParameterModel {
        private String name;
        private String type;
        private boolean required;
    }

    static class TaskModel {
        private String name;
        private String uri;
        private List<OptionModel> options = new ArrayList<>();
        private boolean stopOnFailure = true;
    }

    static class OptionModel {
        private String option;
        private String type;
        private String expression;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileReader reader = new FileReader(new File("C:/github/Soya/pipeline.json"));
        PipelineModel model = gson.fromJson(reader, PipelineModel.class);
        System.out.println(gson.toJson(model));
    }
}
