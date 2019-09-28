package soya.framework.dovetails;

public interface TaskTypeRegistration {
    Class<? extends TaskBuilder> getTaskBuilderType(String schema);
}
