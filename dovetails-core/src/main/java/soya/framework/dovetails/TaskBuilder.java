package soya.framework.dovetails;

public interface TaskBuilder<T extends Task> {
    T create(String uri, ProcessContext context);
}
