package soya.framework.action;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ActionCommandLine {
    private final ActionName actionName;
    private final Map<String, FunctionExpression> options;

    private ActionCommandLine(ActionName actionName, Map<String, FunctionExpression> options) {
        this.actionName = actionName;
        this.options = options;
    }

    public String[] options() {
        return options.keySet().toArray(new String[options.size()]);
    }

    public Map<String, String> parameterMappings() {
        Map<String, String> mappings = new LinkedHashMap<>();
        options.entrySet().forEach(e -> {
            FunctionExpression functionExpression = e.getValue();
            if(functionExpression.getFunction().equals(Function.arg)) {
                mappings.put(functionExpression.expression, e.getKey());
            }
        });

        return mappings;
    }

    public FunctionExpression option(String opt) {
        return options.get(opt);
    }

    public String toURI() {
        StringBuilder builder = new StringBuilder(actionName.toString());
        if (!options.isEmpty()) {
            builder.append("?");
            options.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append(e.getValue()).append("&");
            });
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static Builder builder(Class<? extends ActionCallable> type) {
        return new Builder(type);
    }

    public static Builder builder(URI uri) {
        return new Builder(uri);
    }

    public static Builder builder(String commandline) {
        return new Builder(commandline);
    }

    public static class Builder {
        private ActionName actionName;
        private Map<String, FunctionExpression> options = new LinkedHashMap<>();

        private Builder(Class<? extends ActionCallable> actionType) {
            this.actionName = ActionName.fromClass(actionType);
            Field[] fields = ActionParser.getOptionFields(actionType);
            for (Field field : fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (!commandOption.referenceKey().isEmpty()) {
                    options.put(commandOption.option(), new FunctionExpression(Function.prop, commandOption.referenceKey()));

                } else if (commandOption.required()) {
                    options.put(commandOption.option(), new FunctionExpression(Function.arg, field.getName()));

                } else {
                    options.put(commandOption.option(), new FunctionExpression(Function.val, field.getName()));

                }
            }
        }

        private Builder(URI uri) {
            this.actionName = ActionName.fromURI(uri);
            Map<String, List<String>> params = URIParser.splitQuery(uri.getRawQuery());
            params.entrySet().forEach(entry -> {
                options.put(entry.getKey(), new FunctionExpression(entry.getValue().get(0)));
            });
        }

        private Builder(String commandline) {
            this(URIParser.toURI(commandline));
        }

        public Builder set(String option, String func, String exp) {
            options.put(option, new FunctionExpression(Function.valueOf(func), exp));
            return this;
        }

        public Builder set(String option, String functionExpression) {
            options.put(option, new FunctionExpression(functionExpression));
            return this;
        }

        public ActionCommandLine create() {
            return new ActionCommandLine(actionName, options);
        }
    }

    enum Function {
        arg, prop, ref, res, val
    }

    static class FunctionExpression {
        private final Function function;
        private final String expression;

        private FunctionExpression(Function function, String expression) {
            this.function = function;
            this.expression = expression;
        }

        private FunctionExpression(String exp) {
            String token = exp.trim();
            if (token.contains("(") && token.endsWith(")")) {
                int index = token.indexOf('(');
                this.function = Function.valueOf(token.substring(0, index));
                this.expression = token.substring(index + 1, token.length() - 1);

            } else {
                this.function = Function.val;
                this.expression = token;
            }
        }

        public Function getFunction() {
            return function;
        }

        public String getExpression() {
            return expression;
        }

        public String toString() {
            return new StringBuilder(function.name()).append("(").append(expression).append(")").toString();
        }
    }
}
