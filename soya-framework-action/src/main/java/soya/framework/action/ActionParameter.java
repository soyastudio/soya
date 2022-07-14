package soya.framework.action;

public final class ActionParameter {

    private final String name;
    private final Function function;
    private final String expression;

    private ActionParameter(String name, Function function, String expression) {
        this.name = name;
        this.function = function;
        this.expression = expression;
    }

    private ActionParameter(String name, String exp) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public String getFunction() {
        return function.name();
    }

    public String getExpression() {
        return expression;
    }

    public String toString() {
        return new StringBuilder(function.name()).append("(").append(expression).append(")").toString();
    }

    public static ActionParameter argumentParameter(String name, String exp) {
        return new ActionParameter(name, Function.arg, exp);
    }

    public static ActionParameter propertyParameter(String name, String exp) {
        return new ActionParameter(name, Function.prop, exp);
    }

    public static ActionParameter referenceParameter(String name, String exp) {
        return new ActionParameter(name, Function.ref, exp);
    }

    public static ActionParameter resourceParameter(String name, String exp) {
        return new ActionParameter(name, Function.res, exp);
    }

    public static ActionParameter valueParameter(String name, String exp) {
        return new ActionParameter(name, Function.val, exp);
    }

    public static ActionParameter create(String name, String exp) {
        return new ActionParameter(name, exp);
    }

    enum Function {
        arg, prop, ref, res, val
    }
}
