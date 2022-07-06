package soya.framework.action;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ActionOption {
    private final Field field;

    private final String option;
    private final String longOption;
    private final String description;
    private final boolean required;
    private final Class<?> type;
    private final char valueSeparator;

    private Object defaultValue;

    public ActionOption(Field field) {
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

    public String getOption() {
        return option;
    }

    public String getLongOption() {
        return longOption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public Class<?> getType() {
        return type;
    }

    public char getValueSeparator() {
        return valueSeparator;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        if(type.isInstance(defaultValue)) {
            this.defaultValue = defaultValue;

        } else {
            this.defaultValue = convert(defaultValue);
        }
    }

    public Object get(ActionCallable action) {
        field.setAccessible(true);
        try {
            return field.get(action);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object value, ActionCallable action, boolean setDefaultValue) {
        field.setAccessible(true);
        try {
            if(value == null && setDefaultValue) {
                field.set(action, defaultValue);

            } else if (type.isInstance(value)){
                field.set(action, value);

            } else {
                field.set(action, convert(value));

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object convert(Object object) {
        if(object == null) {
            return null;
        }

        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionOption)) return false;
        ActionOption that = (ActionOption) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
