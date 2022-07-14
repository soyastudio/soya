package soya.framework.action;


import java.lang.reflect.Field;

public class ActionField implements Comparable<ActionField> {
    private final Field field;

    private final String option;
    private final String longOption;
    private final String description;
    private final boolean required;
    private final Class<?> type;
    private final char valueSeparator;

    private Object defaultValue;

    ActionField(Field field) {
        this.field = field;

        CommandOption opt = field.getAnnotation(CommandOption.class);
        if (opt == null) {
            throw new IllegalArgumentException("Field is not annotated as '"
                    + CommandOption.class.getName() + "': " + field.getName());
        }

        this.option = opt.option();
        this.longOption = field.getName();
        this.description = opt.desc();
        this.required = opt.required();
        this.type = field.getType();
        this.valueSeparator = opt.valueSeparator();

        this.defaultValue = convert(opt.defaultValue());

    }

    public Field getField() {
        return field;
    }

    @Override
    public int compareTo(ActionField o) {

        CommandOption commandOption1 = field.getAnnotation(CommandOption.class);
        CommandOption commandOption2 = o.field.getAnnotation(CommandOption.class);

        Class<?> cls1 = field.getDeclaringClass();
        Class<?> cls2 = o.field.getDeclaringClass();

        if (commandOption1.dataForProcessing() && !commandOption2.dataForProcessing()) {
            return 1;

        } else if (!commandOption1.dataForProcessing() && commandOption2.dataForProcessing()) {
            return -1;

        } else {
            int paramDiff = CommandOption.ParamType.indexOf(commandOption1.paramType()) - CommandOption.ParamType.indexOf(commandOption2.paramType());
            if (paramDiff != 0) {
                return paramDiff;

            } else if (cls1.equals(cls2)) {
                return field.getName().compareTo(o.field.getName());

            } else if (cls2.isAssignableFrom(cls1)) {
                return 1;

            } else {
                return -1;
            }

        }

    }

    private Object convert(Object object) {
        if (object == null) {
            return null;
        }

        return object;
    }
}
