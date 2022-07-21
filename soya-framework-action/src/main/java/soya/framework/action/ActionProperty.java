package soya.framework.action;

public final class ActionProperty {

    private final String name;
    private final String option;

    private ActionPropertyType type;
    private String expression;

    private ActionProperty(String name, String option, ActionPropertyType actionPropertyType, String expression) {
        this.name = name;
        this.option = option;
        this.type = actionPropertyType;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public String getOption() {
        return option;
    }

    public ActionPropertyType getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public String toString() {
        return type.name() + "(" + expression + ")";
    }

    static class Builder {
        private final ActionProperty property;

        Builder(ActionProperty property) {
            this.property = property;
        }

        Builder(String name, String option) {
            this.property = new ActionProperty(name, option, ActionPropertyType.arg, name);
        }

        Builder set(String func) {
            String token = func.trim();
            if (token.contains("(") && token.endsWith(")")) {
                int index = token.indexOf('(');
                property.type = ActionPropertyType.valueOf(token.substring(0, index));
                property.expression = token.substring(index + 1, token.length() - 1);

            } else {
                property.type = ActionPropertyType.val;
                property.expression = token;
            }
            return this;
        }

        Builder set(ActionPropertyType type, String expression) {
            property.type = type;
            property.expression = expression;
            return this;
        }

        ActionProperty create() {
            return property;
        }

    }

}
