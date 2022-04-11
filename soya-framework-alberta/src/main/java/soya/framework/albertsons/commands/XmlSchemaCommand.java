package soya.framework.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.document.xmlbeans.XsUtils;
import soya.framework.kt.KnowledgeTreeNode;
import soya.framework.document.xmlbeans.xs.XsNode;

@Command(group = "bod", name = "schema", httpMethod = Command.HttpMethod.GET)
public class XmlSchemaCommand extends SchemaCommand {

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
