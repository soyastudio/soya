package soya.framework;

public interface Session {
    String getId();

    long getCreatedTime();

    // Attribute:
    Object get(String attrName);

    void set(String attrName, Object attrValue) throws IllegalStateException;

    void setImmutable(String attrName, Object attrValue) throws IllegalStateException;

    // Current State
    DataObject getCurrentState();

    void updateState(DataObject state) throws IllegalStateException;

    long getLastUpdatedTime();

    // Evaluation:
    void startEvaluation();

    void endEvaluation();

}
