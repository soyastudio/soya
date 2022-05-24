package soya.framework.commons.util;

public class TextBuilder {
    private int indentLevel = 0;

    public int currentIndentLevel() {
        return indentLevel;
    }

    public TextBuilder pushIndent() {
        indentLevel++;
        return this;
    }

    public TextBuilder popIndent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
        return this;
    }

    public TextBuilder resetIndentLevel() {
        this.indentLevel = indentLevel;
        return this;
    }

}
