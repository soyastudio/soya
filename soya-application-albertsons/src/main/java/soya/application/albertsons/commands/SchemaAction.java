package soya.application.albertsons.commands;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.action.actions.apache.xmlbeans.XmlBeansAction;
import soya.framework.action.actions.apache.xmlbeans.xs.XsNode;
import soya.framework.commons.knowledge.KnowledgeTree;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class SchemaAction<T extends XmlBeansAction> extends BusinessObjectAction {

    private Class<T> type;
    private XmlBeansAction cmd;

    protected KnowledgeTree<SchemaTypeSystem, XsNode> tree;
    protected String result;

    public SchemaAction() {
        super();
        String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
        try {
            type = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    protected String execute() throws Exception {
        XmlBeansAction cmd = type.newInstance();
        Field field = findField("source");
        field.setAccessible(true);
        field.set(cmd, "file:///" + cmmFile);

        this.result = cmd.call().toString();

        Field treeField = findField("tree");
        treeField.setAccessible(true);
        tree = (KnowledgeTree<SchemaTypeSystem, XsNode>) treeField.get(cmd);

        return process(result);
    }

    protected String process(String result) throws Exception {
        return result;
    }

    private Field findField(String name) {
        Field field = null;
        Class<?> clazz = type;
        while (field == null && !clazz.getName().equals("java.lang.Object")) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        return field;
    }
}
