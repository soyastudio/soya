package soya.framework.albertsons.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.transform.schema.KnowledgeTreeNode;
import soya.framework.transform.schema.xs.XsNode;

import java.util.Locale;

@Command(group = "bod", name = "json-type-mappings")
public class XmlToJsonTypeCommand extends SchemaCommand {

    @Override
    protected String render() {
        CodeBuilder codeBuilder = CodeBuilder.newInstance();
        render(tree.root(), codeBuilder);

        return codeBuilder.toString();
    }

    private void render(KnowledgeTreeNode<XsNode> node, CodeBuilder codeBuilder) {
        String jsonType = simpleMapping(XsUtils.type(node.origin()));
        boolean array = !XsUtils.cardinality(node.origin()).endsWith("-1");

        if (array) {
            if ("complex".equals(jsonType)) {
                jsonType = "array";
            } else {
                jsonType = jsonType + "_array";
            }
        }

        if (!jsonType.equalsIgnoreCase("string") && !jsonType.equalsIgnoreCase("complex")) {
            print(node.getPath(), jsonType, codeBuilder);
        }

        node.getChildren().forEach(e -> {
            render(e, codeBuilder);
        });
    }

    protected void print(String path, String type, CodeBuilder codeBuilder) {
        codeBuilder.append(path).append("=").appendLine(type);
    }

    private String simpleMapping(String type) {
        String token = type.toLowerCase(Locale.ROOT);
        switch (token) {
            case "boolean":
                return "boolean";

            case "float":
            case "double":
            case "integer":
            case "long":
            case "int":
            case "short":
            case "byte":
            case "decimal":
            case "nonpositiveinteger":
            case "negativeinteger":
            case "nonnegativeinteger":
            case "positiveinteger":
            case "unsignedlong":
            case "unsignedint":
            case "unsignedshort":
            case "unsignedbyte":
                return "number";

            case "string":
            case "dateTime":
            case "time":
            case "date":
            case "duration":
            case "anyuri":
            case "qname":
            case "base64binary":
            case "hexbinary":
            case "token":
            case "language":
            case "gyearMonth":
            case "gyear":
            case "gmonthday":
            case "gday":
            case "gmonth":
            case "notation":
            case "ncname":
            case "id":
            case "idref":
            case "idrefs":
            case "entity":
            case "entities":
            case "nmtoken":
            case "nmtokens":
                return "string";

            default:
                return "complex";
        }
    }


}
