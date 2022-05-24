package soya.framework.tasks.markdown;

import org.commonmark.node.Document;
import org.commonmark.node.FencedCodeBlock;

public class MarkdownBuilder {
    private Document document;

    private MarkdownBuilder() {
        this.document = new Document();
    }

    public HeadingBuilder heading() {
        return new HeadingBuilder(this);
    }

    public MarkdownBuilder newInstance() {
        return new MarkdownBuilder();
    }

    static class HeadingBuilder {
        private MarkdownBuilder markdownBuilder;
        private int level = 1;
        private String literal;

        private HeadingBuilder(MarkdownBuilder markdownBuilder) {
            this.markdownBuilder = markdownBuilder;
        }

        public HeadingBuilder level(int level) {
            this.level = level;
            return this;
        }

        public HeadingBuilder literal(String literal) {
            this.literal = literal;
            return this;
        }

        public MarkdownBuilder build() {
            // TODO:
            return markdownBuilder;
        }
    }

    public static class ParagraphBuilder {
        private MarkdownBuilder markdownBuilder;
        private String literal;

        private ParagraphBuilder(MarkdownBuilder markdownBuilder) {
            this.markdownBuilder = markdownBuilder;
        }

        public ParagraphBuilder literal(String literal) {
            this.literal = literal;
            return this;
        }

        public MarkdownBuilder build() {
            // TODO:
            return markdownBuilder;
        }
    }

    public static class FencedCodeBlockBuilder {
        private MarkdownBuilder markdownBuilder;

        private FencedCodeBlock fencedCodeBlock;

        private FencedCodeBlockBuilder(MarkdownBuilder markdownBuilder) {
            this.markdownBuilder = markdownBuilder;
            this.fencedCodeBlock = new FencedCodeBlock();
        }

        public FencedCodeBlockBuilder fenceChar(char fenceChar) {
            this.fencedCodeBlock.setFenceChar(fenceChar);
            return this;
        }

        public FencedCodeBlockBuilder fenceLength(int fenceLength) {
            this.fencedCodeBlock.setFenceLength(fenceLength);
            return this;
        }

        public FencedCodeBlockBuilder fenceIndent(int fenceIndent) {
            this.fencedCodeBlock.setFenceIndent(fenceIndent);
            return this;
        }

        public FencedCodeBlockBuilder info(String info) {
            this.fencedCodeBlock.setInfo(info);
            return this;
        }

        public FencedCodeBlockBuilder literal(String literal) {
            this.fencedCodeBlock.setLiteral(literal);
            return this;
        }

        public MarkdownBuilder build() {
            markdownBuilder.document.appendChild(fencedCodeBlock);
            return markdownBuilder;
        }
    }
}
