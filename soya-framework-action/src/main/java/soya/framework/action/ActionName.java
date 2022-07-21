package soya.framework.action;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class ActionName implements Serializable, Comparable<ActionName> {
    private final String group;
    private final String name;

    private ActionName(String group, String name) {
        this.group = group;
        this.name = name;

        if (group == null || group.trim().length() == 0) {
            throw new IllegalArgumentException("Group part cannot be null or empty");
        }

        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Name part cannot be null or empty");
        }
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return group + "://" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionName)) return false;
        ActionName actionName = (ActionName) o;
        return group.equals(actionName.group) && name.equals(actionName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name);
    }

    @Override
    public int compareTo(ActionName o) {
        int result = group.compareTo(o.group);
        if (result == 0) {
            result = name.compareTo(o.name);
        }

        return result;
    }

    public static ActionName fromURI(String uri) {
        try {
            return fromURI(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ActionName fromURI(URI uri) {
        if("class".equalsIgnoreCase(uri.getScheme())) {
            try {
                return fromClass((Class<? extends ActionCallable>) Class.forName(uri.getHost()));

            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return new ActionName(uri.getScheme(), uri.getHost());
    }

    public static ActionName fromClass(Class<? extends ActionCallable> cls) {
        Command command = cls.getAnnotation(Command.class);
        if(command == null) {
            throw new IllegalArgumentException("Class '" + cls.getName() + "' is not annotated as Command");
        }

        return new ActionName(command.group(), command.name());
    }
}
