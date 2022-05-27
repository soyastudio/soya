package soya.application.albertsons.commands;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.commandline.tasks.apache.xmlbeans.XmlBeansTask;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTree;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class SchemaTask<T extends XmlBeansTask> extends BusinessObjectTask {

    private Class<T> type;
    private XmlBeansTask cmd;

    protected KnowledgeTree<SchemaTypeSystem, XsNode> tree;
    protected String result;

    public SchemaTask() {
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
        XmlBeansTask cmd = type.newInstance();
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
