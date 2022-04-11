package soya.framework.document.xmlbeans;

import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.document.xmlbeans.xs.XsNode;
import soya.framework.kt.KnowledgeTreeNode;

@Command(group = "document", name = "xmlbeans-schema-reader", httpRequestTypes = Command.MediaType.TEXT_PLAIN)
public class SchemaReader extends XmlBeansCommand {


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
