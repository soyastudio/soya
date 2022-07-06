package soya.framework.action;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Future;

public final class ActionExecutor implements Executable {

    private final Class<? extends ActionCallable> actionType;
    private final Map<String, ActionOption> options;
    private final ActionOption[] parameters;
    private final boolean async;

    private ActionExecutor(Class<? extends ActionCallable> actionType,
                           Map<String, ActionOption> options,
                           ActionOption[] parameters,
                           boolean async) {
        this.actionType = actionType;
        this.options = options;
        this.parameters = parameters;
        this.async = async;
    }

    public Class<? extends ActionCallable> getActionType() {
        return actionType;
    }

    public Set<ActionOption> getOptions() {
        return new HashSet<>(options.values());
    }

    public ActionOption[] getParameterOptions() {
        return parameters;
    }

    public Class<?>[] getParameterTypes() {
        Class<?>[] paramTypes = new Class[parameters.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = parameters[i].getType();
        }
        return paramTypes;
    }

    public ActionResult execute(Object[] args) throws Exception {
        ActionCallable action = createAction(args);
        if (async) {
            Future<ActionResult> future = ActionContext.getInstance().getExecutorService().submit(action);
            while (!future.isDone()) {
                Thread.sleep(300l);
            }

            return future.get();

        } else {
            return action.call();
        }
    }

    private ActionCallable createAction(Object[] args) throws Exception {
        if (parameters.length != args.length) {
            throw new IllegalArgumentException("TODO");
        }

        ActionCallable action = actionType.newInstance();
        Map<Field, Object> optionValues = new LinkedHashMap<>();
        options.values().forEach(e -> {
            optionValues.put(e.getField(), e.getDefaultValue());
        });

        for (int i = 0; i < parameters.length; i++) {
            optionValues.put(parameters[i].getField(), args[i]);
        }

        optionValues.entrySet().forEach(e -> {
            try {
                Field field = e.getKey();
                field.setAccessible(true);
                field.set(action, e.getValue());
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });

        return action;
    }

    public static Builder builder(String commandline) {
        return new Builder(commandline);
    }

    public static Builder builder(URI uri) {
        return new Builder(uri);
    }

    public static Builder builder(Class<? extends ActionCallable> actionType) {
        return new Builder(actionType);
    }

    public static class Builder {

        private Class<? extends ActionCallable> actionType;
        private Map<String, ActionOption> options = new HashMap<>();

        private List<ActionOption> parameters = new ArrayList<>();
        private boolean async;

        private Builder(String commandline) {
            this(URIParser.toURI(commandline));
        }

        private Builder(URI uri) {
            fromClass(ActionContext.getInstance().getActionType(ActionName.fromURI(uri)));
            String query = uri.getQuery();
            if (query != null && query.trim().length() > 0) {
                try {
                    List<ActionParameter> actionParameters = new ArrayList<>();
                    Map<String, List<String>> params = URIParser.splitQuery(query, "UTF-8");
                    params.entrySet().forEach(e -> {
                        String opt = e.getKey();
                        String val = e.getValue().get(0);
                        if (options.containsKey(opt)) {
                            if (val.startsWith("[") && val.endsWith("]")) {
                                String ph = val.substring(1, val.length() - 1).trim();
                                if (StringUtils.isNumeric(ph)) {
                                    actionParameters.add(new ActionParameter(options.get(opt), Integer.parseInt(ph)));
                                }


                            } else {
                                options.get(opt).setDefaultValue(val);
                            }
                        }
                    });

                    Collections.sort(actionParameters);
                    actionParameters.forEach(e -> {
                        parameters.add(e.actionOption);
                    });

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private Builder(Class<? extends ActionCallable> actionType) {
            fromClass(actionType);
        }

        private void fromClass(Class<? extends ActionCallable> actionType) {
            this.actionType = actionType;
            Command command = actionType.getAnnotation(Command.class);
            if (command == null) {
                throw new IllegalArgumentException("Class is not defined as Command: " + actionType.getName());
            }

            Field[] fields = ActionParser.getOptionFields(actionType);
            for (Field field : fields) {
                ActionOption actionOption = new ActionOption(field);
                if (actionOption.getOption() != null) {
                    options.put(actionOption.getOption(), actionOption);
                }
                options.put(actionOption.getLongOption(), actionOption);
            }
        }

        public ActionOption getActionOption(String opt) {
            return options.get(opt);
        }

        public Collection<ActionOption> getActionOptions() {
            return new HashSet<>(options.values());
        }

        public Builder setOptionDefaultValue(String option, Object value) {
            if (options.get(option) == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }
            options.get(option).setDefaultValue(value);
            return this;
        }

        public Builder defineParameter(String option) {
            if (options.get(option) == null) {
                throw new IllegalArgumentException("Option is not defined: " + option);
            }

            ActionOption actionOption = options.get(option);
            if (parameters.contains(actionOption)) {
                throw new IllegalArgumentException("Option is already defined: " + option);
            }

            parameters.add(actionOption);
            return this;
        }

        public Builder async(Boolean async) {
            this.async = async;
            return this;
        }

        public ActionExecutor create() {
            return new ActionExecutor(actionType, options, parameters.toArray(new ActionOption[parameters.size()]), async);
        }
    }

    static class ActionParameter implements Comparable<ActionParameter> {
        private ActionOption actionOption;
        private int sort;

        ActionParameter(ActionOption actionOption, int sort) {
            this.actionOption = actionOption;
            this.sort = sort;
        }

        @Override
        public int compareTo(ActionParameter o) {
            return sort - o.sort;
        }
    }

    public static void main(String[] args) throws Exception {

        String commandline = "text-util://base64-encode -s [1] -c utf-8";
        URI uri = URI.create("text-util://base64-decode?s=[1]&c=[3]");

        String text = "Hello World!";
        String encoded = "SGVsbG8gV29ybGQh";

        ActionResult result = ActionExecutor.builder(commandline)
                .create().execute(new Object[]{text});

        System.out.println(result.result());

        ActionResult result2 = ActionExecutor.builder(uri)
                .create().execute(new Object[]{encoded, "utf-8"});
        System.out.println(result2.result());
    }
}
