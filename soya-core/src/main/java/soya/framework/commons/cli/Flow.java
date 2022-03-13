package soya.framework.commons.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.cli.Options;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flow {

    private static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER = (cause, session) -> cause.printStackTrace();
    private final static Evaluator DEFAULT_EVALUATOR = new DefaultEvaluator();

    private final String name;
    private final CommandExecutor executor;
    private final ExceptionHandler exceptionHandler;

    private List<Task> tasks;

    private Flow(String name, CommandExecutor executor, List<Task> tasks, ExceptionHandler exceptionHandler) {
        this.name = name;
        this.executor = executor;
        this.tasks = tasks;
        this.exceptionHandler = exceptionHandler;
    }

    public void execute(Callback callback) {
        Properties props = executor.context().properties();
        Resources.compile(props);

        Enumeration<?> propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            String propValue = props.getProperty(propName);
            if (propValue.contains("${") && propValue.contains("}")) {
                throw new IllegalStateException("Property cannot evaluate: '" + propName + "=" + propValue + "'");
            } else {
                executor.context().setProperty(propName, propValue);
            }
        }

        DefaultSession session = new DefaultSession(executor.context().properties());
        execute(session, callback);
    }

    public void execute(Object input, Callback callback) throws Exception {
        DefaultSession session = new DefaultSession(executor.context().properties());
        if (input instanceof Properties) {
            Properties props = (Properties) input;
            session.properties.putAll(props);
            session.evaluate();

        } else if (input instanceof String) {
            String source = (String) input;
            if (Resources.isFile(source)) {
                String extension = Resources.getFileExtension(source);
                if ("properties".equalsIgnoreCase(extension)) {
                    Properties props = new Properties();
                    props.load(new FileInputStream(new File(source)));
                    session.properties.putAll(props);
                    session.evaluate();

                } else if ("json".equalsIgnoreCase(extension)) {

                } else if ("xml".equalsIgnoreCase(extension)) {

                } else if ("xsd".equalsIgnoreCase(extension)) {

                } else if ("yaml".equalsIgnoreCase(extension)
                        || "yml".equalsIgnoreCase(extension)) {

                }
            }

        }

        execute(session, callback);
    }

    private void execute(DefaultSession session, Callback callback) {
        long timestamp = System.currentTimeMillis();
        for (Task task : tasks) {
            session.executed.add(task.configuration.getName());
            session.cursor = task.configuration.getName();

            String cmd = task.configuration.getCommand();

            String[] args = task.compiler.compile(task.configuration, session);

            Future<String> future = executor.submit(cmd, args);
            while (!future.isDone()) {
                try {
                    Thread.sleep(50l);
                } catch (InterruptedException e) {
                    session.onException(e, exceptionHandler);
                }
            }

            try {
                String result = future.get();
                session.onSuccess(task.getName(), result, task.callback);

            } catch (InterruptedException e) {
                session.onException(e, exceptionHandler);

            } catch (ExecutionException e) {
                session.onException(e, exceptionHandler);

            } catch (Exception e) {
                session.onException(e, exceptionHandler);
            }
        }

        if (callback != null) {
            try {
                callback.onSuccess(session);
            } catch (Exception e) {
                session.onException(e, exceptionHandler);
            }
        }

        System.out.println("Flow executed in " + (System.currentTimeMillis() - timestamp) + "ms.");
    }

    public static FlowBuilder builder() {
        return builder(null, null);
    }

    public static FlowBuilder builder(Properties properties) {
        return builder(null, properties);
    }

    public static FlowBuilder builder(ExecutorService executorService, Properties properties) {
        CommandExecutor.Builder builder = CommandExecutor.builder(CommandCallable.class)
                .scan("soya.framework");

        if (executorService != null) {
            builder.setExecutorService(executorService);
        }

        if (properties != null) {
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                builder.setProperty(key, value);
            }
        }

        return new FlowBuilder(builder.create());
    }

    public static FlowBuilder builder(CommandExecutor executor) {
        return new FlowBuilder(executor);
    }

    public static FlowBuilder builder(File file) throws IOException {
        FlowBuilder builder = builder();
        DefaultFlowLoader loader = new DefaultFlowLoader();
        loader.load(builder, new FileInputStream(file));
        return builder;
    }

    public static CallbackChain callbacks() {
        return new CallbackChain();
    }

    public static class Task {
        private final Configuration configuration;

        private Compiler compiler;
        private Callback callback;

        public Task(Configuration configuration, Compiler compiler, Callback callback) {
            this.configuration = configuration;
            this.compiler = compiler;
            this.callback = callback;
        }

        public String getName() {
            return configuration.getName();
        }

        public void execute(Session session) {

        }

        public static TaskBuilder builder(Class<? extends CommandCallable> commandType) {
            return new TaskBuilder(commandType);
        }

    }

    public interface Session {
        String getId();

        long startTime();

        Properties properties();

        String cursor();

        String[] executed();

        String[] results();

        Object getResult(String taskName);

        Object getAttribute(String name);

        void setAttribute(String name, Object value);

    }

    public interface Configuration {
        String getName();

        String getCommand();

        Options getOptions();

        void evaluator(String option, String exp);

        void evaluator(String option, String exp, Evaluator evaluator);

        String evaluate(String option, Session session);
    }

    public interface Compiler {
        String[] compile(Configuration configuration, Session session);
    }

    public interface Callback {
        void onSuccess(Session session) throws Exception;
    }

    public interface ExceptionHandler {
        void onException(Throwable cause, Session session);
    }

    public interface Evaluator {
        String evaluate(String exp, Session session);
    }

    public static class FlowBuilder {
        private String name;
        private CommandExecutor executor;
        private ExceptionHandler exceptionHandler = DEFAULT_EXCEPTION_HANDLER;
        private List<Task> tasks = new ArrayList<>();

        private FlowBuilder(CommandExecutor executor) {
            this.executor = executor;
            this.name = executor.context().name();
        }

        public Class<? extends CommandCallable> getCommandType(String uri) {
            return executor.context().getCommandType(uri);
        }

        public FlowBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FlowBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public FlowBuilder setProperty(String key, String value) {
            executor.context().setProperty(key, value);
            return this;
        }

        public FlowBuilder addTask(Task task) {
            tasks.add(task);
            return this;
        }

        public Flow create() {
            return new Flow(name, executor, tasks, exceptionHandler);
        }
    }

    public interface FlowLoader {
        void load(FlowBuilder builder, InputStream inputStream) throws IOException;
    }

    public static class TaskBuilder {
        private Class<? extends CommandCallable> commandType;
        private Configuration configuration;
        private Compiler compiler;
        private Callback callback;

        private TaskBuilder(Class<? extends CommandCallable> commandType) {
            this.commandType = commandType;
            this.configuration = new DefaultConfiguration(commandType);
        }

        public TaskBuilder name(String name) {
            ((DefaultConfiguration) configuration).name = name;
            return this;
        }

        public TaskBuilder setOption(String option, String value) {
            configuration.evaluator(option, value);
            return this;
        }

        public TaskBuilder setOption(String option, String exp, Evaluator evaluator) {
            configuration.evaluator(option, exp, evaluator);
            return this;
        }

        public TaskBuilder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Task create() {
            if (compiler == null) {
                this.compiler = new DefaultCompiler();
            }

            return new Task(configuration, compiler, callback);
        }
    }

    static class DefaultSession implements Session {
        private final String id;
        private final long startTime;
        private final Properties properties = new Properties();

        private List<String> executed = new ArrayList<>();
        private String cursor;
        private Map<String, Object> results = new LinkedHashMap<>();

        private Map<String, Object> attributes = new HashMap<>();

        private DefaultSession(Properties properties) {
            this.id = UUID.randomUUID().toString();
            this.startTime = System.currentTimeMillis();
            this.properties.putAll(properties);

            evaluate();

        }

        public void evaluate() {
            Resources.compile(this.properties);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public long startTime() {
            return startTime;
        }

        @Override
        public Properties properties() {
            return properties;
        }

        @Override
        public String cursor() {
            return cursor;
        }

        @Override
        public String[] executed() {
            return executed.toArray(new String[executed.size()]);
        }

        @Override
        public String[] results() {
            return results.keySet().toArray(new String[results.size()]);
        }

        @Override
        public Object getResult(String taskName) {
            return results.get(taskName);
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(String name, Object value) {
            if (value != null) {
                attributes.put(name, value);

            } else {
                attributes.remove(name);
            }

        }

        private void onSuccess(String task, String result, Callback callback) throws Exception {
            results.put(task, result);
            if (callback != null) {
                callback.onSuccess(this);
            }
        }

        private void onException(Exception e, ExceptionHandler exceptionHandler) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, this);
            }
        }
    }

    static class DefaultConfiguration implements Configuration {
        private Class<? extends CommandCallable> commandType;
        private String name;
        private String command;
        private Options options;

        private Map<String, String> expressions = new HashMap<>();
        private Map<String, Evaluator> evaluators = new HashMap<>();

        DefaultConfiguration(Class<? extends CommandCallable> commandType) {
            this.commandType = commandType;
            Command command = commandType.getAnnotation(Command.class);
            this.command = command.name();
            this.name = command.name();
            this.options = CommandRunner.parse(commandType);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getCommand() {
            return command;
        }

        @Override
        public Options getOptions() {
            return options;
        }

        @Override
        public void evaluator(String option, String exp) {
            this.expressions.put(option, exp);

        }

        @Override
        public void evaluator(String option, String exp, Evaluator evaluator) {
            this.expressions.put(option, exp);
            if (evaluator != null) {
                evaluators.put(option, evaluator);
            }

        }

        @Override
        public String evaluate(String option, Session session) {
            String exp = expressions.get(option);
            Evaluator evaluator = evaluators.containsKey(option) ? evaluators.get(option) : DEFAULT_EVALUATOR;

            return evaluator.evaluate(exp, session);
        }
    }

    static class DefaultCompiler implements Compiler {

        @Override
        public String[] compile(Configuration configuration, Session session) {
            List<String> list = new ArrayList<>();
            configuration.getOptions().getOptions().forEach(e -> {
                String opt = e.getOpt();
                String value = configuration.evaluate(opt, session);
                if (value != null) {
                    list.add("-" + opt);
                    list.add(value);

                }
            });

            return list.toArray(new String[list.size()]);
        }
    }

    public static class DefaultEvaluator implements Evaluator {
        private final String regex = "\\$\\{([A-Za-z_.][A-Za-z0-9_.]*)}";
        private final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        @Override
        public String evaluate(String exp, Session session) {
            String expression = exp;

            if (expression != null && expression.contains("${")) {
                StringBuffer buffer = new StringBuffer();
                Matcher matcher = pattern.matcher(expression);
                while (matcher.find()) {
                    String token = matcher.group(1);
                    String value = getValue(token, session);
                    matcher.appendReplacement(buffer, value);
                }
                matcher.appendTail(buffer);
                expression = buffer.toString();
            }

            return expression;
        }

        private String getValue(String attribute, Session session) {
            Properties properties = session.properties();

            if (attribute.startsWith(".")) {
                // FIXME:
                return (String) session.getResult(attribute.substring(1));

            } else if (properties.getProperty(attribute) != null) {
                return properties.getProperty(attribute);

            } else if (System.getProperty(attribute) != null) {
                return System.getProperty(attribute);

            }


            throw new IllegalArgumentException("Cannot find attribute on current context: " + attribute);
        }
    }

    public static class CallbackChain implements Callback {
        private List<Callback> callbacks = new ArrayList<>();

        private CallbackChain() {
        }

        public CallbackChain add(Callback callback) {
            callbacks.add(callback);
            return this;
        }

        @Override
        public void onSuccess(Session session) throws Exception {
            for (Callback callback : callbacks) {
                callback.onSuccess(session);
            }

        }
    }

    public static class DefaultFlowLoader implements FlowLoader {

        @Override
        public void load(FlowBuilder builder, InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            ObjectBuilder objectBuilder = null;
            while (line != null) {

                if (line.startsWith("name: ")) {
                    flush(builder, objectBuilder);

                    String name = line.substring("name: ".length()).trim();
                    builder.name(name);

                } else if (line.startsWith("properties:")) {
                    flush(builder, objectBuilder);
                    objectBuilder = new ObjectBuilder("properties");

                } else if (line.startsWith("tasks:")) {
                    flush(builder, objectBuilder);
                    objectBuilder = new ObjectBuilder("tasks");

                } else if (line.startsWith("callbacks:")) {
                    flush(builder, objectBuilder);
                    objectBuilder = new ObjectBuilder("callbacks");

                } else if (line.startsWith("  - ")) {
                    objectBuilder.add(line);

                } else {
                    if (objectBuilder != null) {
                        objectBuilder.set(line);
                    }
                }

                line = reader.readLine();
            }

            if (objectBuilder != null) {
                flush(builder, objectBuilder);
            }

        }

        private void flush(FlowBuilder builder, ObjectBuilder objectBuilder) {
            if (objectBuilder != null) {

                objectBuilder.build(builder);

            }

        }
    }

    static class ObjectBuilder {
        private String type;
        private List<List<String>> list = new ArrayList<>();

        private List<String> current;

        ObjectBuilder(String type) {
            this.type = type;
        }

        void add(String line) {
            List<String> object = new ArrayList<>();
            list.add(object);
            current = object;
            set(line);
        }

        void set(String line) {
            current.add(line);
        }

        List<String> getCurrent() {
            return current;
        }

        void build(FlowBuilder builder) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if ("properties".equals(type)) {
                list.forEach(o -> {
                    String key = null;
                    String value = null;
                    for (String ln : o) {
                        if (ln.startsWith("  - key: ")) {
                            key = ln.substring("  - key: ".length()).trim();
                        } else if ((ln.startsWith("    value: "))) {
                            value = ln.substring("    value: ".length()).trim();
                        }
                    }

                    if (key != null && value != null) {
                        builder.setProperty(key, value);

                    } else {
                        throw new IllegalArgumentException("");
                    }

                });
            } else if ("tasks".equals(type)) {
                list.forEach(o -> {
                    JsonObject jsonObject = createTask(o);
                    JsonElement je = jsonObject.get("uri");
                    if (je != null) {
                        String uri = je.getAsString();
                        Class<? extends CommandCallable> cls = builder.getCommandType(uri);
                        if (cls == null) {
                            throw new IllegalArgumentException("Cannot find command type with uri: " + uri);
                        }

                        TaskBuilder taskBuilder = Task.builder(cls);
                        if (jsonObject.get("options") != null) {
                            JsonObject options = jsonObject.get("options").getAsJsonObject();
                            options.entrySet().forEach(en -> {
                                taskBuilder.setOption(en.getKey(), en.getValue().getAsString());
                            });
                        }

                        taskBuilder.name(jsonObject.get("task").getAsString());

                        builder.addTask(taskBuilder.create());

                    }
/*
                    TaskBuilder taskBuilder = Task.builder();
                    jsonObject.entrySet().forEach(e -> {
                        String k = e.getKey();
                        JsonElement v = e.getValue();

                        if(k.)

                    });*/
                });
            }
        }

        private JsonObject createTask(List<String> lines) {
            JsonObject jsonObject = new JsonObject();
            for (String ln : lines) {
                if (ln.startsWith("  - task: ")) {
                    jsonObject.addProperty("task", ln.substring("  - task: ".length()).trim());

                } else if (ln.startsWith("    uri: ")) {
                    jsonObject.addProperty("uri", ln.substring("    uri: ".length()).trim());

                } else if (ln.trim().endsWith("options:")) {
                    jsonObject.add("options", new JsonObject());

                } else if (ln.startsWith("      ") && ln.contains(": ")) {
                    if (jsonObject.get("options") != null) {
                        JsonObject options = jsonObject.getAsJsonObject("options").getAsJsonObject();
                        String token = ln.trim();
                        int index = token.indexOf(": ");
                        String k = token.substring(0, index);
                        String v = token.substring(index + 2);
                        options.addProperty(k, v);

                    }
                }
            }

            return jsonObject;
        }

    }
}
