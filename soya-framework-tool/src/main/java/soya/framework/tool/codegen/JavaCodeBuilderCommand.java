package soya.framework.tool.codegen;

import soya.framework.commons.util.CodeBuilder;

public abstract class JavaCodeBuilderCommand extends JavaCodegenCommand {
    protected int indent;

    @Override
    public String call() throws Exception {
        CodeBuilder builder = CodeBuilder.newInstance();
        printPackage(builder);
        printImports(builder);
        printClass(builder);
        indent ++;
        printBody(builder);
        indent --;
        return end(builder);
    }

    protected abstract void printBody(CodeBuilder builder);

    protected void printPackage(CodeBuilder builder) {
        builder.append("package ").append(packageName).appendLine(";").appendLine();
    }

    protected void printImports(CodeBuilder builder) {

    }

    protected void printClass(CodeBuilder builder) {
        builder.append("public class ").append(className).appendLine(" {").appendLine();

    }

    protected void printDefaultConstructor(String modify, CodeBuilder builder) {
        builder.append(modify, indent).append(" ").append(className).appendLine("() {");

        builder.appendLine("}", indent).appendLine();
    }

    protected String end(CodeBuilder builder) {
        builder.append("}");
        return builder.toString();

    }
}
