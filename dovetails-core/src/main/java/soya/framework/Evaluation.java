package soya.framework;

public interface Evaluation {
    Session getSession();

    Object get(String attrName);

    void set(String attrName, Object attrValue);

    DataObject getValue();

    void setValue(DataObject value);

}
