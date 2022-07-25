package soya.framework.commons.knowledge.renderers;

import soya.framework.commons.knowledge.*;
import soya.framework.util.CodeBuilder;

public abstract class HtmlTreeRenderer implements KnowledgeSystem.KnowledgeRenderer<String> {

    @Override
    public String render(KnowledgeTree knowledgeTree) throws KnowledgeProcessException {
        KnowledgeTreeNode<?> root = (KnowledgeTreeNode<?>) knowledgeTree.root();

        CodeBuilder builder = CodeBuilder.newInstance();

        builder.append("<!DOCTYPE html>");
        builder.appendLine("<html>");

        builder.appendLine("<head>");
        builder.appendLine("</head>");

        builder.appendLine("<body>");
        printTreeNode(root, builder);
        builder.appendLine("</body>");
        builder.appendLine("</html>");

        return builder.toString();
    }

    protected abstract void printHead(KnowledgeTree knowledgeTree, CodeBuilder builder);

    protected abstract void printKnowledgeNode(KnowledgeNode kn, CodeBuilder builder, int level);

    protected void printTreeNode(KnowledgeTreeNode<?> treeNode, CodeBuilder builder) {
        int level = indent(treeNode);
        builder.append("<div class=\"", level).append("ktn-" + level).append("\"").appendLine(">");

        printKnowledgeNode(treeNode.getData(), builder, level);

        treeNode.getChildren().forEach(e -> {
            printTreeNode(e, builder);
        });

        builder.appendLine("</div>", level);
    }

    private int indent(KnowledgeTreeNode<?> treeNode) {
        return treeNode.getPath().split("/").length;
    }

}
