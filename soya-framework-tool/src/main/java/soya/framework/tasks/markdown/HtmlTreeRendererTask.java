package soya.framework.tasks.markdown;

import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.knowledge.KnowledgeTreeNode;

@Command(group = "markdown", name = "html-tree", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_XML)
public class HtmlTreeRendererTask extends MarkdownTask {

    @Override
    protected Object process() throws Exception {

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

    protected void printTreeNode(KnowledgeTreeNode<?> treeNode, CodeBuilder builder) {
        int level = indent(treeNode);
        builder.append("<div class=\"", level).append("ktn-" + level).append("\"").appendLine(">");

        treeNode.getChildren().forEach(e -> {
            printTreeNode(e, builder);
        });

        builder.appendLine("</div>", level);

    }

    private int indent(KnowledgeTreeNode<?> treeNode) {
        return treeNode.getPath().split("/").length;
    }

}
