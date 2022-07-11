package soya.framework.action;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
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

    public static Pipeline fromJson(Reader reader) {
        Builder builder = builder();
        PipelineModel model = new Gson().fromJson(JsonParser.parseReader(reader), PipelineModel.class);
        if (model.parameters != null) {
            for (ParameterModel param : model.parameters) {
                try {
                    builder.addParameter(param.name, Class.forName(param.type));
                } catch (ClassNotFoundException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        }

        if (model.tasks == null || model.tasks.length == 0) {
            throw new IllegalArgumentException("No tasks in this pipeline.");
        }

        for (TaskModel taskModel : model.tasks) {
            TaskBuilder taskBuilder = builder.builderTask(URI.create(taskModel.uri));
            if (taskModel.options != null) {
                for (OptionModel opt : taskModel.options) {
                    if (opt.value != null) {
                        taskBuilder.setOptionValue(opt.option, opt.value);
                    } else if (opt.parameter != null) {
                        taskBuilder.actionBuilder.defineParameter(opt.option);
                        taskBuilder.setOptionFromParameter(opt.option, opt.parameter);

                    } else if (opt.property != null) {
                        taskBuilder.setOptionFromProperty(opt.option, opt.property);
                    } else if (opt.expression != null) {
                        taskBuilder.setOptionFromSession(opt.option, opt.expression);
                    }
                }
            }

            taskBuilder.add(taskModel.name, taskModel.stopOnFailure);
        }

        builder.result = model.result;

        return builder.create();
    }

    public static Pipeline fromJson(String json) {
        Builder builder = builder();
        PipelineModel model = new Gson().fromJson(json, PipelineModel.class);

        if (model.parameters != null) {
            for (ParameterModel param : model.parameters) {
                try {
                    builder.addParameter(param.name, Class.forName(param.type));
                } catch (ClassNotFoundException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        }

        if (model.tasks == null || model.tasks.length == 0) {
            throw new IllegalArgumentException("No tasks in this pipeline.");
        }

        for (TaskModel taskModel : model.tasks) {
            TaskBuilder taskBuilder = builder.builderTask(URI.create(taskModel.uri));
            if (taskModel.options != null) {
                for (OptionModel opt : taskModel.options) {
                    if (opt.value != null) {
                        taskBuilder.setOptionValue(opt.option, opt.value);
                    } else if (opt.parameter != null) {
                        taskBuilder.setOptionFromParameter(opt.option, opt.parameter);
                    } else if (opt.property != null) {
                        taskBuilder.setOptionFromProperty(opt.option, opt.property);
                    } else if (opt.expression != null) {
                        taskBuilder.setOptionFromSession(opt.option, opt.expression);
                    }
                }
            }

            taskBuilder.add(taskModel.name, taskModel.stopOnFailure);
        }

        builder.result = model.result;

        return builder.create();
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

        public TaskBuilder builderTask(String commandline) {
            return new TaskBuilder(this, commandline);
        }

        public TaskBuilder builderTask(URI uri) {
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
        private ActionExecutor.Builder actionBuilder;

        private Map<ActionOption, OptionSetting> optionSettings = new LinkedHashMap<>();

        private TaskBuilder(Builder builder, String commandline) {
            this.builder = builder;
            this.actionBuilder = ActionExecutor.builder(commandline);
        }

        private TaskBuilder(Builder builder, URI uri) {
            this.builder = builder;
            this.actionBuilder = ActionExecutor.builder(uri);
        }

        public TaskBuilder setOptionValue(String option, Object value) {
            ActionOption actionOption = actionBuilder.getActionOption(option);
            if (actionOption == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }

            if (optionSettings.containsKey(actionOption)) {
                throw new IllegalArgumentException("Option is already set: " + option);
            }

            optionSettings.put(actionOption, OptionSetting.value(value));
            return this;
        }

        public TaskBuilder setOptionFromParameter(String option, String parameter) {
            ActionOption actionOption = actionBuilder.getActionOption(option);
            if (actionOption == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }

            if (optionSettings.containsKey(actionOption)) {
                throw new IllegalArgumentException("Option is already set: " + option);
            }

            optionSettings.put(actionOption, OptionSetting.fromParam(parameter));
            return this;
        }

        public TaskBuilder setOptionFromProperty(String option, String property) {
            ActionOption actionOption = actionBuilder.getActionOption(option);
            if (actionOption == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }

            if (optionSettings.containsKey(actionOption)) {
                throw new IllegalArgumentException("Option is already set: " + option);
            }

            optionSettings.put(actionOption, OptionSetting.fromProperty(property));
            return this;
        }

        public TaskBuilder setOptionFromSession(String option, String expression) {
            ActionOption actionOption = actionBuilder.getActionOption(option);
            if (actionOption == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }

            if (optionSettings.containsKey(actionOption)) {
                throw new IllegalArgumentException("Option is already set: " + option);
            }

            optionSettings.put(actionOption, OptionSetting.fromSession(expression));
            return this;
        }

        public Builder add(String name, boolean stopOnFailure) {
            builder.tasks.add(new Task(name, actionBuilder.create(), stopOnFailure, optionSettings));
            return builder;
        }
    }

    static class Task {
        private final String name;
        private final ActionExecutor actionExecutor;
        private final boolean stopOnFailure;

        private Map<ActionOption, OptionSetting> optionSettings;

        Task(String name, ActionExecutor actionExecutor, boolean stopOnFailure, Map<ActionOption, OptionSetting> optionSettings) {
            this.name = name;
            this.actionExecutor = actionExecutor;
            this.stopOnFailure = stopOnFailure;

            this.optionSettings = optionSettings;
        }
    }

    static class OptionSetting {
        private ActionOption actionOption;

        private Object value;
        private String parameter;
        private String property;
        private String expression;

        public static OptionSetting value(Object value) {
            OptionSetting setting = new OptionSetting();
            setting.value = value;
            return setting;
        }

        public static OptionSetting fromParam(String parameter) {
            OptionSetting setting = new OptionSetting();
            setting.parameter = parameter;
            return setting;
        }

        public static OptionSetting fromProperty(String property) {
            OptionSetting setting = new OptionSetting();
            setting.parameter = property;
            return setting;
        }

        public static OptionSetting fromSession(String expression) {
            OptionSetting setting = new OptionSetting();
            setting.expression = expression;
            return setting;
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

            Map<ActionOption, Object> optionValues = new HashMap<>();
            task.actionExecutor.getOptions().forEach(e -> {
                if (task.optionSettings.containsKey(e)) {
                    OptionSetting setting = task.optionSettings.get(e);

                    if (setting.value != null) {
                        optionValues.put(e, setting.value);

                    } else if (setting.parameter != null) {
                        optionValues.put(e, session.parameterValues.get(setting.parameter));
                    } else if (setting.property != null) {
                        optionValues.put(e, ActionContext.getInstance().getProperty(setting.property));
                    } else if (setting.expression != null) {
                        optionValues.put(e, session.evaluate(setting.expression));
                    }
                } else {
                    optionValues.put(e, e.getDefaultValue());
                }
            });

            ActionCallable action = task.actionExecutor.getActionType().newInstance();
            optionValues.entrySet().forEach(e -> {
                Field field = e.getKey().getField();
                field.setAccessible(true);
                try {
                    field.set(action, e.getValue());
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });

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

    public static void main(String[] args) throws Exception {
        File file = new File("C:/github/Soya/pipeline.json");
        Pipeline pipeline = Pipeline.fromJson(new FileReader(file));
        ActionResult result = pipeline.execute(new Object[]{"Hello World!"});

        System.out.println("------------------ " + result.result());
    }
}
