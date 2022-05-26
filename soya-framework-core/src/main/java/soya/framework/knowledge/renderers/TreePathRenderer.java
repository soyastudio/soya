package soya.framework.knowledge.renderers;

import soya.framework.knowledge.KnowledgeNode;
import soya.framework.knowledge.KnowledgeProcessException;
import soya.framework.knowledge.KnowledgeSystem;
import soya.framework.knowledge.KnowledgeTree;

import java.lang.reflect.Method;

public class TreePathRenderer implements KnowledgeSystem.KnowledgeRenderer<String> {
    private String methodName;

    public TreePathRenderer getValueMethod(String methodName) {
        this.methodName = methodName;
        return this;
    }

    @Override
    public String render(KnowledgeTree knowledgeTree) throws KnowledgeProcessException {
        StringBuilder builder = new StringBuilder();
        knowledgeTree.paths().forEachRemaining(e -> {
            KnowledgeNode<?> treeNode = (KnowledgeNode<?>) knowledgeTree.get((String) e);
            Object origin = treeNode.origin();
            String value = "";

            try {
                Method method = origin.getClass().getMethod(methodName, new Class[0]);
                value = method.invoke(origin, new Object[0]).toString();
            } catch (Exception ex) {

            }

            builder.append(e).append("=").append(value).append("\n");
        });

        return builder.toString();
    }
}
