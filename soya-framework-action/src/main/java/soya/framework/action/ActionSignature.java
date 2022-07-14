package soya.framework.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ActionSignature {

    private final ActionName actionName;
    private final Map<String, ActionParameter> options;

    private Map<String, String> arguments = new LinkedHashMap<>();

    private ActionSignature(ActionName actionName, Map<String, ActionParameter> options) {
        this.actionName = actionName;
        this.options = options;
        options.entrySet().forEach(e -> {
            ActionParameter actionParameter = e.getValue();
            if (actionParameter.getFunction().equals(ActionParameter.Function.arg)) {
                arguments.put(actionParameter.getExpression(), e.getKey());
            }
        });
    }

    public ActionName getActionName() {
        return actionName;
    }

    public String[] argumentNames() {
        return arguments.keySet().toArray(new String[arguments.size()]);
    }

    public String getOptionName(String arg) {
        return arguments.get(arg);
    }

    public String[] options() {
        return options.keySet().toArray(new String[options.size()]);
    }

    public ActionParameter option(String opt) {
        return options.get(opt);
    }

    public URI toURI() {
        StringBuilder builder = new StringBuilder(actionName.toString());
        if (!options.isEmpty()) {
            builder.append("?");
            options.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append(e.getValue()).append("&");
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
            values.put(arguments.get(argNames[i]), args[i]);

        }

        ActionClass actionClass = ActionClass.get(actionName);
        Constructor constructor = actionClass.getActionType().getConstructor(new Class[0]);
        constructor.setAccessible(true);
        ActionCallable action = (ActionCallable) constructor.newInstance(new Object[0]);
        Field[] fields = actionClass.getActionFields();
        for (Field field : fields) {
            Object value = null;
            ActionParameter actionParameter = options.get(field.getName());
            ActionParameter.Function function = ActionParameter.Function.valueOf(actionParameter.getFunction());
            if (function.equals(ActionParameter.Function.arg)) {
                value = values.get(field.getName());

            } else if (function.equals(ActionParameter.Function.prop)) {
                value = ActionContext.getInstance().getProperty(actionParameter.getExpression());

            } else if (function.equals(ActionParameter.Function.ref)) {
                throw new IllegalStateException("Cannot evaluate parameter: '" + field.getName() + "=" + actionParameter.toString() + "'.");

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
        private Map<String, ActionParameter> options = new LinkedHashMap<>();

        private Builder(ActionClass actionClass) {
            this.actionName = actionClass.getActionName();
            for (String fn : actionClass.getActionFieldNames()) {
                Field field = actionClass.getActionField(fn);
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                ActionParameter parameter;
                if (!commandOption.referenceKey().isEmpty()) {
                    parameter = ActionParameter.propertyParameter(field.getName(), commandOption.referenceKey());

                } else if (commandOption.required()) {
                    parameter = ActionParameter.argumentParameter(field.getName(), field.getName());

                } else if (ActionResource.class.isAssignableFrom(field.getType())) {
                    parameter = ActionParameter.resourceParameter(field.getName(), commandOption.defaultValue());

                } else {
                    parameter = ActionParameter.valueParameter(field.getName(), commandOption.defaultValue());

                }
                options.put(fn, parameter);
            }

        }

        private Builder(URI uri) {
            this.actionName = ActionName.fromURI(uri);
            Map<String, List<String>> params = URIParser.splitQuery(uri.getRawQuery());
            params.entrySet().forEach(entry -> {
                options.put(entry.getKey(), ActionParameter.create(entry.getKey(), entry.getValue().get(0)));
            });
        }

        private Builder(String commandline) {
            this(URIParser.toURI(commandline));
        }

        public Builder addArgumentOption(String option, String paramName) {
            options.put(option, ActionParameter.argumentParameter(option, paramName));
            return this;
        }

        public Builder addPropertyOption(String option, String propertyName) {
            options.put(option, ActionParameter.propertyParameter(option, propertyName));
            return this;
        }

        public Builder addResourceOption(String option, String propertyName) {
            options.put(option, ActionParameter.resourceParameter(option, propertyName));
            return this;
        }

        public Builder addReferenceOption(String option, String propertyName) {
            options.put(option, ActionParameter.referenceParameter(option, propertyName));
            return this;
        }

        public Builder addValueOption(String option, String propertyName) {
            options.put(option, ActionParameter.propertyParameter(option, propertyName));
            return this;
        }

        public Builder set(String option, String functionExpression) {
            options.put(option, ActionParameter.create(option, functionExpression));
            return this;
        }

        public ActionSignature create() {
            return new ActionSignature(actionName, options);
        }
    }

    public static void main(String[] args) {
        ActionSignature signature = ActionSignature.builder("s").create();
    }
}
