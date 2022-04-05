package soya.framework.commons.util;

public class CodeBuilder {
    private static String[] indents = new String[]{
            "",
            "\t",
            "\t\t",
            "\t\t\t",
            "\t\t\t\t",
            "\t\t\t\t\t",
            "\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
    };

    private StringBuilder builder;
    private int indentLevel;

    private CodeBuilder(StringBuilder builder) {
        this.builder = builder;
    }

    public CodeBuilder append(String s) {
        builder.append(s);
        return this;
    }

    public CodeBuilder append(String s, int indent) {
        builder.append(indents[indent]).append(s);
        return this;
    }

    public CodeBuilder appendLine() {
        builder.append("\n");
        return this;
    }

    public CodeBuilder appendLine(String s) {
        builder.append(s).append("\n");
        return this;
    }

    public CodeBuilder appendLine(String s, int indent) {
        builder.append(indents[indent]).append(s).append("\n");
        return this;
    }

    public int currentIndentLevel() {
        return indentLevel;
    }

    public void pushIndent() {
        indentLevel ++;
    }

    public void popIndent() {
        if(indentLevel > 0) {
            indentLevel --;
        }
    }

    public void setCurrentIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public String toString() {
        return builder.toString();
    }

    public static CodeBuilder newInstance() {
        return new CodeBuilder(new StringBuilder());
    }
}
