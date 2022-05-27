package soya.framework.commandline.tasks.apache.xmlbeans;

import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTreeNode;

@Command(group = "transform", name = "xmlbeans-schema-reader", httpRequestTypes = Command.MediaType.TEXT_PLAIN)
public class SchemaReader extends XmlBeansTask {


    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        CodeBuilder codeBuilder = CodeBuilder.newInstance();
        render(tree.root(), codeBuilder);

        return codeBuilder.toString();
    }

    private void render(KnowledgeTreeNode<XsNode> node, CodeBuilder codeBuilder) {
        codeBuilder.append(node.getPath())
                .append("=").append("type(").append(XsUtils.type(node.origin())).append(")")
                .append("::").append("cardinality(").append(XsUtils.cardinality(node.origin())).appendLine(")");
        node.getChildren().forEach(e -> {
            render(e, codeBuilder);
        });
    }
}
