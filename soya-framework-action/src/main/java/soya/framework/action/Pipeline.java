package soya.framework.action;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Pipeline implements Executable {

    private LinkedHashMap<String, Class<?>> parameterTypes;
    private List<Task> tasks;
    private String result;

    private Pipeline(LinkedHashMap<String, Class<?>> parameterTypes, List<Task> tasks, String result) {
        this.parameterTypes = parameterTypes;
        this.tasks = tasks;
        this.result = result;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LinkedHashMap<String, Class<?>> parameterTypes = new LinkedHashMap<>();
        private List<Task> tasks = new ArrayList<>();
        private String result;

        private Builder() {
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
            return new Pipeline(parameterTypes, tasks, result);
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

        public TaskBuilder addArgumentOption(String option, String value) {
            signatureBuilder.addArgumentOption(option, value);
            return this;
        }

        public TaskBuilder addPropertyOption(String option, String propertyName) {
            signatureBuilder.addPropertyOption(option, propertyName);
            return this;
        }

        public TaskBuilder addReferenceOption(String option, String propertyName) {
            signatureBuilder.addReferenceOption(option, propertyName);
            return this;
        }

        public TaskBuilder addResourceOption(String option, String propertyName) {
            signatureBuilder.addResourceOption(option, propertyName);
            return this;
        }

        public TaskBuilder addValueOption(String option, String propertyName) {
            signatureBuilder.addValueOption(option, propertyName);
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
                ActionParameter actionParameter = task.signature.option(field.getName());
                ActionParameter.Function function = ActionParameter.Function.valueOf(actionParameter.getFunction());
                if (function.equals(ActionParameter.Function.arg)) {
                    value = session.parameterValues.get(actionParameter.getExpression());

                } else if (function.equals(ActionParameter.Function.prop)) {
                    value = ActionContext.getInstance().getProperty(actionParameter.getExpression());

                } else if (function.equals(ActionParameter.Function.ref)) {
                    value = session.evaluate(actionParameter.getExpression());

                } else if (function.equals(ActionParameter.Function.res)) {
                    Resource resource = Resource.create(actionParameter.getExpression());

                } else if (function.equals(ActionParameter.Function.val)) {
                    value = actionParameter.getExpression();

                }

                if (value != null) {
                    field.setAccessible(true);
                    field.set(action, value);
                }

            }

            ActionResult result = action.call();
            session.results.put(task.name, result);

            return result.successful();

        }}


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
        ParameterModel[] parameters;
        TaskModel[] tasks;
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
        private OptionModel[] options;
        private boolean stopOnFailure = true;
    }

    static class OptionModel {
        private String option;
        private String value;
        private String parameter;
        private String property;
        private String expression;
    }
}
