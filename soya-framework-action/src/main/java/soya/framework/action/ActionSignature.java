package soya.framework.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

public final class ActionSignature {

    private final ActionName actionName;
    private final Map<String, ActionProperty> shortOptions;
    private final Map<String, ActionProperty> longOptions;
    private Map<String, String> arguments = new LinkedHashMap<>();

    private ActionSignature(ActionName actionName, List<ActionProperty> options, List<ActionProperty> argumentParams) {
        this.actionName = actionName;

        this.shortOptions = new LinkedHashMap<>();
        this.longOptions = new LinkedHashMap<>();
        options.forEach(opt -> {
            shortOptions.put(opt.getOption(), opt);
            longOptions.put(opt.getName(), opt);
        });

        argumentParams.forEach(e -> {
            arguments.put(e.getExpression(), e.getName());
        });
    }

    public ActionName getActionName() {
        return actionName;
    }

    public String[] argumentNames() {
        return arguments.keySet().toArray(new String[arguments.size()]);
    }

    public ActionProperty getArgumentParameter(String arg) {
        ActionProperty param = longOptions.get(arguments.get(arg));
        if (param == null) {
            throw new IllegalArgumentException("Argument is not defined: " + arg);
        }
        return param;
    }

    public Collection<ActionProperty> parameters() {
        return longOptions.values();
    }

    public ActionProperty getParameter(String opt) {
        if (shortOptions.containsKey(opt)) {
            return shortOptions.get(opt);
        } else if (longOptions.containsKey(opt)) {
            return longOptions.get(opt);
        } else {
            throw new IllegalArgumentException("Action parameter is not defined: " + opt);
        }
    }

    public URI toURI() {
        StringBuilder builder = new StringBuilder(actionName.toString());
        if (!longOptions.isEmpty()) {
            builder.append("?");
            arguments.entrySet().forEach(e -> {
                ActionProperty parameter = getParameter(e.getValue());
                builder.append(parameter.getOption())
                        .append("=")
                        .append(parameter.toString())
                        .append("&");
            });

            longOptions.values().forEach(e -> {
                if (!ActionPropertyType.arg.equals(e.getType())) {
                    builder.append(e.getOption())
                            .append("=")
                            .append(e.toString())
                            .append("&");
                }
            });

            builder.deleteCharAt(builder.length() - 1);
        }

        return URI.create(builder.toString());
    }

    public ActionCallable create(Object[] args) throws Exception {
        if (args == null && arguments.size() != 0 || arguments.size() != args.length) {
            throw new IllegalArgumentException("Input arguments length does not match the argument definition");
        }

        Map<String, Object> values = new Hashtable<>();
        String[] argNames = argumentNames();
        for (int i = 0; i < argNames.length; i++) {
            if(args[i] != null) {
                values.put(arguments.get(argNames[i]), args[i]);

            }

        }

        ActionClass actionClass = ActionClass.get(actionName);
        Constructor constructor = actionClass.getActionType().getConstructor(new Class[0]);
        constructor.setAccessible(true);
        ActionCallable action = (ActionCallable) constructor.newInstance(new Object[0]);
        Field[] fields = actionClass.getActionFields();
        for (Field field : fields) {
            Object value = null;
            ActionProperty actionProperty = getParameter(field.getName());
            if (actionProperty.getType().equals(ActionPropertyType.arg)) {
                value = values.get(field.getName());

            } else if (actionProperty.getType().equals(ActionPropertyType.prop)) {
                value = ActionContext.getInstance().getProperty(actionProperty.getExpression());

            } else if (actionProperty.getType().equals(ActionPropertyType.ref)) {
                throw new IllegalStateException("Cannot evaluate parameter: '" + field.getName() + "=" + actionProperty.toString() + "'.");

            } else if (actionProperty.getType().equals(ActionPropertyType.res)) {
                Resource resource = Resource.create(actionProperty.getExpression());

            } else if (actionProperty.getType().equals(ActionPropertyType.val)) {
                value = actionProperty.getExpression();

            }

            if (value != null) {
                field.setAccessible(true);
                field.set(action, value);
            }
        }

        return action;

    }

    public static Builder builder(ActionClass actionClass) {
        return new Builder(actionClass);
    }

    public static Builder builder(URI uri) {
        return new Builder(uri);
    }

    public static Builder builder(String commandline) {
        return new Builder(commandline);
    }

    public static class Builder {
        private ActionName actionName;
        private Map<String, ActionProperty> shortOptions = new LinkedHashMap<>();
        private Map<String, ActionProperty> longOptions = new LinkedHashMap<>();
        private List<ActionProperty> argumentParameters = new ArrayList<>();

        private Builder(ActionClass actionClass) {
            this.actionName = actionClass.getActionName();
            for (Field field : actionClass.getActionFields()) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                ActionProperty.Builder paramBuilder = new ActionProperty.Builder(field.getName(), commandOption.option());
                if (!commandOption.referenceKey().isEmpty()) {
                    paramBuilder.set(ActionPropertyType.prop, commandOption.referenceKey());

                }

                ActionProperty parameter = paramBuilder.create();
                longOptions.put(field.getName(), parameter);
                shortOptions.put(commandOption.option(), parameter);
                if (parameter.getType().equals(ActionPropertyType.arg)) {
                    argumentParameters.add(parameter);
                }
            }
        }

        private Builder(URI uri) {
            this.actionName = ActionName.fromURI(uri);
            ActionClass actionClass = ActionClass.get(actionName);
            Map<String, List<String>> params = URIParser.splitQuery(uri.getRawQuery());

            LinkedHashMap<String, String> args = new LinkedHashMap();
            params.entrySet().forEach(e -> {
                args.put(e.getValue().get(0), e.getKey());
            });

            for (Field field : actionClass.getActionFields()) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                ActionProperty.Builder paramBuilder = new ActionProperty.Builder(field.getName(), commandOption.option());
                if (args.containsKey(field.getName())) {
                    paramBuilder.set(ActionPropertyType.arg, args.get(field.getName()));

                } else if (!commandOption.referenceKey().isEmpty()) {
                    paramBuilder.set(ActionPropertyType.prop, commandOption.referenceKey());

                } else {
                    paramBuilder.set(ActionPropertyType.val, commandOption.defaultValue());
                }

                ActionProperty parameter = paramBuilder.create();
                longOptions.put(field.getName(), parameter);
                shortOptions.put(commandOption.option(), parameter);
                if (parameter.getType().equals(ActionPropertyType.arg)) {
                    argumentParameters.add(parameter);
                }
            }


        }

        private Builder(String commandline) {
            this(URIParser.toURI(commandline));
        }

        public Builder set(String option, String functionExpression) {
            ActionProperty parameter;
            if(shortOptions.containsKey(option)) {
                parameter = shortOptions.get(option);
            } else if(longOptions.containsKey(option)) {
                parameter = longOptions.get(option);
            } else {
                throw new IllegalArgumentException("Action parameter is not defined: " + option);
            }

            parameter = new ActionProperty.Builder(parameter).set(functionExpression).create();
            if(parameter.getType().equals(ActionPropertyType.arg)) {
                if(argumentParameters.contains(parameter)) {
                    throw new IllegalArgumentException("Argument is already defined: " + option);
                } else {
                    argumentParameters.add(parameter);
                }
            }

            return this;
        }

        public Builder set(String option, ActionPropertyType type, String exp) {
            ActionProperty parameter;
            if(shortOptions.containsKey(option)) {
                parameter = shortOptions.get(option);
            } else if(longOptions.containsKey(option)) {
                parameter = longOptions.get(option);
            } else {
                throw new IllegalArgumentException("Action parameter is not defined: " + option);
            }

            parameter = new ActionProperty.Builder(parameter).set(type, exp).create();
            if(parameter.getType().equals(ActionPropertyType.arg)) {
                if(argumentParameters.contains(parameter)) {
                    throw new IllegalArgumentException("Argument is already defined: " + option);
                } else {
                    argumentParameters.add(parameter);
                }
            }

            return this;
        }

        public ActionSignature create() {
            return new ActionSignature(actionName, new ArrayList<>(longOptions.values()), argumentParameters);
        }
    }

    public static void main(String[] args) {
        ActionSignature signature = ActionSignature.builder("s").create();
    }
}
