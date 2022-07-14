package soya.framework.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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

    private String arraySeparator = ", ";
    private DateFormat dateFormat;
    private List<TemplateWrapper<?>> templates = new ArrayList<>();


    private CodeBuilder(StringBuilder builder) {
        this.builder = builder;
    }

    public CodeBuilder setArraySeparator(String separator) {
        this.arraySeparator = separator;
        return this;
    }

    public CodeBuilder setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public <T> CodeBuilder addTemplate(Template<T> template, Class<T> type) {
        templates.add(new TemplateWrapper<>(type, template));
        return this;
    }

    private Template<?> getTemplate(Object o) {
        for(TemplateWrapper wrapper: templates) {
            if(wrapper.type.isInstance(o)) {
                return wrapper.template;
            }
        }

        return null;
    }

    public int currentIndentLevel() {
        return indentLevel;
    }

    public CodeBuilder pushIndent() {
        indentLevel++;
        return this;
    }

    public CodeBuilder popIndent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
        return this;
    }

    public CodeBuilder setCurrentIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
        return this;
    }

    public CodeBuilder resetIndent() {
        this.indentLevel = 0;
        return this;
    }

    public CodeBuilder appendWithCurrentIndent(String s) {
        builder.append(indents[indentLevel]).append(s);
        return this;
    }

    //
    public CodeBuilder append(String s) {
        if (s != null) {
            builder.append(s);
        }
        return this;
    }

    public CodeBuilder appendIndent(int indentLevel) {
        builder.append(indents[indentLevel]);
        return this;
    }

    public CodeBuilder appendLine() {
        builder.append("\n");
        return this;
    }

    public CodeBuilder append(String s, int indent) {
        builder.append(indents[indent]).append(s);
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

    //
    public CodeBuilder append(Object o, int indent) {
        if (indent > 0) {
            builder.append(indents[indent]);
        }
        builder.append(o);
        return this;
    }

    public CodeBuilder appendWithCondition(Object o, boolean condition) {
        if (condition) {
            builder.append(o);
        }

        return this;
    }

    public String toString() {
        return builder.toString();
    }

    public static CodeBuilder newInstance() {
        return new CodeBuilder(new StringBuilder());
    }

    static class TemplateWrapper<T> {
        private final Class<T> type;
        private final Template<T> template;

        private TemplateWrapper(Class<T> type, Template<T> template) {
            this.type = type;
            this.template = template;
        }


    }

    interface Template<T> {
        String toString(T o);
    }
}
