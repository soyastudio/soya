package soya.framework.commandline;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class TaskName implements Serializable, Comparable<TaskName> {
    private final String group;
    private final String name;

    public TaskName(String group, String name) {
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
        if (!(o instanceof TaskName)) return false;
        TaskName taskName = (TaskName) o;
        return group.equals(taskName.group) && name.equals(taskName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name);
    }

    @Override
    public int compareTo(TaskName o) {
        int result = group.compareTo(o.group);
        if (result == 0) {
            result = name.compareTo(o.name);
        }

        return result;
    }

    public static TaskName fromURI(String uri) {
        try {
            return fromURI(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static TaskName fromURI(URI uri) {
        if("class".equalsIgnoreCase(uri.getScheme())) {
            try {
                return fromTaskClass((Class<? extends TaskCallable>) Class.forName(uri.getHost()));

            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return new TaskName(uri.getScheme(), uri.getHost());
    }

    public static TaskName fromTaskClass(Class<? extends TaskCallable> cls) {
        Command command = cls.getAnnotation(Command.class);
        if(command == null) {
            throw new IllegalArgumentException("Class '" + cls.getName() + "' is not annotated as Command");
        }

        return new TaskName(command.group(), command.name());
    }
}
