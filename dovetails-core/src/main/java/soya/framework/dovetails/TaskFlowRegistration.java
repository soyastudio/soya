package soya.framework.dovetails;

public interface TaskFlowRegistration {
    String[] taskFlows();

    TaskFlow getTaskFlow(String uri);
}
