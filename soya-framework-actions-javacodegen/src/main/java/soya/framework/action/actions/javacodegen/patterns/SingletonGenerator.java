package soya.framework.action.actions.javacodegen.patterns;

import soya.framework.action.Command;
import soya.framework.action.actions.javacodegen.JavaCodeBuilderAction;
import soya.framework.util.CodeBuilder;

@Command(group = "java-codegen", name = "singleton")
public class SingletonGenerator extends JavaCodeBuilderAction {

    @Override
    protected void printBody(CodeBuilder builder) {

        builder.append("private static ", indent).append(className).append(" INSTANCE").appendLine(";");
        builder.appendLine();

        builder.appendLine("static {", indent);
        builder.append("INSTANCE = new ", indent + 1).append(className).appendLine("();");

        builder.appendLine("}", indent).appendLine();

        printDefaultConstructor("protected", builder);

        builder.append("public static ", indent).append(className).appendLine(" getInstance() {");
        builder.appendLine("return INSTANCE;", indent + 1);
        builder.appendLine("}", indent);

    }
}
