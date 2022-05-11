package soya.framework.tasks.markdown;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import soya.framework.core.CommandGroup;
import soya.framework.core.CommandOption;
import soya.framework.core.Task;

@CommandGroup(group = "markdown",
        title = "Markdown Tool",
        description = "Markdown parsing and conversion toolkit.")
public abstract class MarkdownTask<T> extends Task<T> implements MarkdownEventListener {

    @CommandOption(option = "m", required = true, dataForProcessing = true)
    protected String markdown;

    protected Node document;

    @Override
    protected void init() throws Exception {
        this.document = Parser.builder().build().parse(markdown);
    }

    @Override
    public T execute() throws Exception {
        document.accept(new MarkdownVisitor(this));
        return process();
    }

    protected abstract T process() throws Exception;

    static class MarkdownVisitor extends AbstractVisitor {

        private MarkdownEventListener listener;

        MarkdownVisitor(MarkdownEventListener listener) {
            this.listener = listener;
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            listener.onEvent(new MarkdownEvent(blockQuote));
            super.visit(blockQuote);

        }

        @Override
        public void visit(BulletList bulletList) {
            listener.onEvent(new MarkdownEvent(bulletList));
            super.visit(bulletList);
        }

        @Override
        public void visit(Code code) {
            listener.onEvent(new MarkdownEvent(code));
            super.visit(code);
        }

        @Override
        public void visit(Document document) {
            listener.onEvent(new MarkdownEvent(document));
            super.visit(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            listener.onEvent(new MarkdownEvent(emphasis));
            super.visit(emphasis);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            listener.onEvent(new MarkdownEvent(fencedCodeBlock));
            super.visit(fencedCodeBlock);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            listener.onEvent(new MarkdownEvent(hardLineBreak));
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            listener.onEvent(new MarkdownEvent(heading));
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            listener.onEvent(new MarkdownEvent(thematicBreak));
            super.visit(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            listener.onEvent(new MarkdownEvent(htmlInline));
            super.visit(htmlInline);
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            listener.onEvent(new MarkdownEvent(htmlBlock));
            super.visit(htmlBlock);
        }

        @Override
        public void visit(Image image) {
            listener.onEvent(new MarkdownEvent(image));
            super.visit(image);
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            listener.onEvent(new MarkdownEvent(indentedCodeBlock));
            super.visit(indentedCodeBlock);
        }

        @Override
        public void visit(Link link) {
            listener.onEvent(new MarkdownEvent(link));
            super.visit(link);
        }

        @Override
        public void visit(ListItem listItem) {
            listener.onEvent(new MarkdownEvent(listItem));
            super.visit(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            listener.onEvent(new MarkdownEvent(orderedList));
            super.visit(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            listener.onEvent(new MarkdownEvent(paragraph));
            super.visit(paragraph);

        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            listener.onEvent(new MarkdownEvent(softLineBreak));
            super.visit(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            listener.onEvent(new MarkdownEvent(strongEmphasis));
            super.visit(strongEmphasis);
        }

        @Override
        public void visit(Text text) {
            listener.onEvent(new MarkdownEvent(text));
            super.visit(text);


            System.out.println("----------------- " + text.getParent());
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            listener.onEvent(new MarkdownEvent(linkReferenceDefinition));
            super.visit(linkReferenceDefinition);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            listener.onEvent(new MarkdownEvent(customBlock));
            super.visit(customBlock);
        }

        @Override
        public void visit(CustomNode customNode) {
            listener.onEvent(new MarkdownEvent(customNode));
            super.visit(customNode);
        }
    }

}
