package soya.application.albertsons.commands;

import com.google.common.base.CaseFormat;
import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTreeNode;

@Command(group = "business-object-development", name = "esql-template",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EsqlTemplateTask extends ConstructTask {
    private static String namespace = "http://collab.safeway.com/it/architecture/info/default.aspx";

    private static String PATTERN_CAST_DECIMAL = "CAST(%s AS DECIMAL(10, 2))";

    private String inputRootVariable = "_inputRootNode";
    private String inputRootReference = "InputRoot.JSON.Data";

    private String outputRootName;
    private String outputRootVariable;

    @Override
    protected String render() {
        String brokerSchema = bod.getFlows().get(0).getPackageName();
        String module = bod.getFlows().get(0).getTransformer() + "_Compute";

        outputRootName = tree.root().getName();
        outputRootVariable = outputRootName + "_";

        outputRootName = tree.root().getName();
        outputRootVariable = outputRootName + "_";

        CodeBuilder builder = CodeBuilder.newInstance();
        if (brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append("\n\n");
        }
        builder.append("CREATE COMPUTE MODULE ").appendLine(module);
        builder.appendLine();

        // UDP:
        builder.appendLine("-- Declare UDPs", 1);
        builder.appendLine("DECLARE VERSION_ID EXTERNAL CHARACTER '1.0.0';", 1);
        builder.appendLine("DECLARE SYSTEM_ENVIRONMENT_CODE EXTERNAL CHARACTER 'PROD';", 1);

        builder.appendLine();

        // Namespace
        declareNamespace(builder);

        builder.appendLine("CREATE FUNCTION Main() RETURNS BOOLEAN", 1);
        begin(builder, 1);

        // Declare Input Root
        builder.appendLine("-- Declare Input Message Root", 2);
        builder.appendLine("DECLARE " + inputRootVariable + " REFERENCE TO " + inputRootReference + ";", 2);
        builder.appendLine();

        // Declare Output Domain
        builder.appendLine("-- Declare Output Message Root", 2);
        builder.append("CREATE LASTCHILD OF OutputRoot DOMAIN ", 2).append("'XMLNSC'").appendLine(";").appendLine();

        builder.append("DECLARE ", 2).append(outputRootVariable).append(" REFERENCE TO OutputRoot.XMLNSC.").append(outputRootName).appendLine(";");
        builder.append("CREATE LASTCHILD OF OutputRoot.", 2).append("XMLNSC AS ").append(outputRootVariable).append(" TYPE XMLNSC.Folder NAME '").append(outputRootName).append("'").appendLine(";");
        builder.append("SET OutputRoot.XMLNSC.", 2).append(outputRootName).appendLine(".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;");
        builder.appendLine();

        //print node:
        tree.root().getChildren().forEach(e -> {
            printNode(e, builder, 2);
        });

        // closing
        builder.appendLine("RETURN TRUE;", 2);
        builder.appendLine("END;", 1);
        builder.appendLine();
        builder.appendLine("END MODULE;");

        return builder.toString();
    }

    private void declareNamespace(CodeBuilder builder) {
        builder.appendLine("-- Declare Namespace", 1);
        builder.appendLine("DECLARE " + "Abs" + " NAMESPACE " + "'" + namespace + "'" + ";", 1);
        builder.appendLine();
    }

    private void begin(CodeBuilder builder, int indent) {
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }

        builder.append("BEGIN").append("\n");
    }

    private void printNode(KnowledgeTreeNode<XsNode> node, CodeBuilder builder, int indent) {
        if (node.getAnnotation(CONSTRUCTION_NAMESPACE) != null) {
            if (node.getParent() != null) {
                printConstruction(node, builder, indent);
            }

        } else if (node.getAnnotation(ASSIGNMENT_NAMESPACE) != null) {
            printAssignment(node, builder, indent);

        } else {
            return;

        }
    }

    private void printAssignment(KnowledgeTreeNode<XsNode> node, CodeBuilder builder, int indent) {

        Assignment assignment = (Assignment) node.getAnnotation(ASSIGNMENT_NAMESPACE);
        Construction construction = (Construction) node.getParent().getAnnotation(CONSTRUCTION_NAMESPACE);

        builder.append("-- ", indent).appendLine(node.getPath());

        String type = "(XMLNSC.Field)";
        String name = node.getName();
        String assign = assignment.getAssign();

        if (assign.endsWith("[*]")) {
            printSimpleArray(node, builder, indent);

        } else {
            if (assign.startsWith("$.")) {
                if (assign.contains("[*].")) {
                    String arrayPath = assign.substring(0, assign.lastIndexOf("[*]") + 3);
                    Array array = arrayMap.get(arrayPath);
                    if (array != null) {
                        assign = array.getVariable() + assign.substring(arrayPath.length());
                    }
                } else {
                    assign = inputRootVariable + assign.substring(1);
                }

            } else if ("???".equals(assign)) {
                assign = "'???'";
            }

            if (XsNode.XsNodeType.Attribute.equals(node.origin().getNodeType())) {
                type = "(XMLNSC.Attribute)";
                if (name.startsWith("@")) {
                    name = name.substring(1);
                }
            }

            if (node.origin().getName().getNamespaceURI() != null && node.origin().getName().getNamespaceURI().trim().length() > 0) {
                type = type + "Abs:";
            }

            if ("decimal".equals(mappings.get(node.getPath()).type) && !node.origin().getSchemaType().isBuiltinType()) {
                assign = String.format(PATTERN_CAST_DECIMAL, assign);
            }

            builder.append("SET ", indent)
                    .append(construction.getVariable()).append(".").append(type).append(name)
                    .append(" = ")
                    .append(assign).appendLine(";")
                    .appendLine();
        }
    }

    private void printConstruction(KnowledgeTreeNode<XsNode> node, CodeBuilder builder, int indent) {

        Construction construction = (Construction) node.getAnnotation(CONSTRUCTION_NAMESPACE);
        if (construction.arrays().size() > 0) {
            printArray(node, builder, indent);

        } else {
            Construction parent = (Construction) node.getParent().getAnnotation(CONSTRUCTION_NAMESPACE);
            String name = node.getName();
            if (namespace.equals(node.origin().getName().getNamespaceURI())) {
                name = "Abs:" + name;
            }

            builder.append("-- ", indent).appendLine(node.getPath());
            builder.append("DECLARE ", indent)
                    .append(construction.getVariable())
                    .append(" REFERENCE TO ")
                    .append(parent.getVariable()).appendLine(";");

            builder.append("CREATE LASTCHILD OF ", indent)
                    .append(parent.getVariable())
                    .append(" AS ")
                    .append(construction.getVariable())
                    .append(" TYPE XMLNSC.Folder NAME '")
                    .append(name)
                    .appendLine("';")
                    .appendLine();

            node.getChildren().forEach(e -> {
                printNode(e, builder, indent + 1);
            });
        }
    }

    private void printArray(KnowledgeTreeNode<XsNode> node, CodeBuilder builder, int indent) {
        Construction construction = (Construction) node.getAnnotation(CONSTRUCTION_NAMESPACE);
        Construction parent = (Construction) node.getParent().getAnnotation(CONSTRUCTION_NAMESPACE);
        final String name = "Abs:" + node.getName();
        construction.arrays().forEach(a -> {
            String eval = a.getSourcePath().substring(0, a.getSourcePath().length() - 3);
            if (eval.contains("[*]")) {
                int index = eval.lastIndexOf("[*]");
                String parentArrayPath = eval.substring(0, index + 3);
                Array parentArray = arrayMap.get(parentArrayPath);
                if (parentArray != null) {
                    eval = parentArray.getVariable() + eval.substring(index + 3) + ".Item";
                }

            } else {
                eval = inputRootVariable + eval.substring(1) + ".Item";

            }

            builder.append("-- LOOP ", indent).append(a.getSourcePath()).append(" TO ").append(node.getPath()).appendLine();
            builder.append("DECLARE ", indent).append(a.getVariable()).append(" REFERENCE TO ").append(eval).appendLine(";");
            builder.append(a.getName(), indent).append(" : WHILE LASTMOVE(").append(a.getVariable()).appendLine(") DO").appendLine();

            builder.append("-- ", indent + 1).appendLine(node.getPath());
            builder.append("DECLARE ", indent + 1)
                    .append(construction.getVariable())
                    .append(" REFERENCE TO ")
                    .append(parent.getVariable()).appendLine(";");

            builder.append("CREATE LASTCHILD OF ", indent + 1)
                    .append(parent.getVariable())
                    .append(" AS ")
                    .append(construction.getVariable())
                    .append(" TYPE XMLNSC.Folder NAME '")
                    .append(name)
                    .appendLine("';")
                    .appendLine();


            node.getChildren().forEach(e -> {
                printNode(e, builder, indent + 2);
            });

            builder.append("MOVE ", indent).append(a.getVariable()).appendLine(" NEXTSIBLING;");
            builder.append("END WHILE ", indent).append(a.getName()).appendLine(";");
            builder.appendLine("-- END LOOP", indent).appendLine();

        });
    }

    private void printSimpleArray(KnowledgeTreeNode<XsNode> node, CodeBuilder builder, int indent) {
        Assignment assignment = (Assignment) node.getAnnotation(ASSIGNMENT_NAMESPACE);
        Construction parent = (Construction) node.getParent().getAnnotation(CONSTRUCTION_NAMESPACE);
        final String name = "Abs:" + node.getName();

        String assign = assignment.getAssign();
        String nameBase = node.getName();
        String arrName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, nameBase).toUpperCase() + "_LOOP";
        String arrVar = "_" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, nameBase).toLowerCase() + "_item";
        String var = nameBase + "_";

        String eval = assign.substring(0, assign.length() - 3);
        if (eval.contains("[*]")) {
            int index = eval.lastIndexOf("[*]");
            String parentArrayPath = eval.substring(0, index + 3);
            Array parentArray = arrayMap.get(parentArrayPath);
            if (parentArray != null) {
                eval = parentArray.getVariable() + eval.substring(index + 3) + ".Item";
            }

        } else {
            eval = inputRootVariable + eval.substring(1) + ".Item";

        }

        builder.append("-- LOOP ", indent).append(assignment.getAssign()).append(" TO ").append(node.getPath()).appendLine();

        builder.append("DECLARE ", indent).append(arrVar).append(" REFERENCE TO ").append(eval).appendLine(";");
        builder.append("DECLARE ", indent).append(var).append(" REFERENCE TO ").append(parent.getVariable()).appendLine(";");

        builder.append(arrName, indent).append(" : WHILE LASTMOVE(").append(arrVar).appendLine(") DO").appendLine();

        builder.append("-- ", indent + 1).appendLine(node.getPath());
        builder.append("CREATE LASTCHILD OF ", indent + 1)
                .append(parent.getVariable())
                .append(" AS ")
                .append(var)
                .append(" TYPE XMLNSC.Field NAME '")
                .append(name)
                .append("'")
                .append(" VALUE ")
                .append(arrVar)
                .appendLine(";")
                .appendLine();


        builder.append("MOVE ", indent).append(arrVar).appendLine(" NEXTSIBLING;");
        builder.append("END WHILE ", indent).append(arrName).appendLine(";");
        builder.appendLine("-- END LOOP", indent).appendLine();
    }

}
