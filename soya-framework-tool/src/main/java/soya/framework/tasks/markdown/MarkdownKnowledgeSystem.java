package soya.framework.tasks.markdown;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import soya.framework.kt.KnowledgeBuildException;
import soya.framework.kt.KnowledgeTree;
import soya.framework.kt.generic.GenericKnowledgeSystem;
import soya.framework.kt.generic.GenericKnowledgeTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;

public class MarkdownKnowledgeSystem extends GenericKnowledgeSystem<Document, MarkdownNode> {

    protected MarkdownKnowledgeSystem(Object source, KnowledgeTree knowledge) {
        super(source, knowledge);
    }

    public static KnowledgeTree<Document, MarkdownNode> knowledgeTree(Object source) throws KnowledgeBuildException {
        return (KnowledgeTree<Document, MarkdownNode>) builder(source)
                .knowledgeExtractor(new KnowledgeExtractor<Document>() {
                    @Override
                    public Document extract(Object src) throws IOException, KnowledgeBuildException {
                        if (src instanceof String) {
                            return (Document) Parser.builder().build().parse((String) src);

                        } else if (src instanceof Reader) {
                            return (Document) Parser.builder().build().parseReader((Reader) src);

                        } else if (src instanceof InputStream) {
                            return (Document) Parser.builder().build().parseReader(new InputStreamReader((InputStream) src));

                        } else {
                            throw new KnowledgeBuildException("Cannot parse source type: " + src.getClass().getName());
                        }
                    }
                })
                .knowledgeDigester(new DefaultMarkdownDigester())
                .create()
                .getKnowledge();
    }

    static class DefaultMarkdownDigester extends AbstractVisitor implements KnowledgeDigester<Document, MarkdownNode> {

        private Stack<MarkdownNode> stack = new Stack<>();

        @Override
        public KnowledgeTree<Document, MarkdownNode> digester(Document knowledge) throws KnowledgeBuildException {
            MarkdownNode root = new MarkdownNode(knowledge);
            stack.push(root);

            KnowledgeTree<Document, MarkdownNode> tree = GenericKnowledgeTree.newInstance(knowledge, "", root);
            knowledge.accept(this);

            stack.clear();
            return tree;
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            super.visit(blockQuote);
        }

        @Override
        public void visit(BulletList bulletList) {
            super.visit(bulletList);
        }

        @Override
        public void visit(Code code) {
            super.visit(code);
        }

        @Override
        public void visit(Document document) {
            super.visit(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            super.visit(emphasis);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            super.visit(fencedCodeBlock);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            MarkdownNode node = new MarkdownNode(heading);

            MarkdownNode last = stack.peek();
            while (last.getLevel() <= node.getLevel()) {
                last = stack.pop();
            }

            stack.push(node);
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            super.visit(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            super.visit(htmlInline);
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            super.visit(htmlBlock);
        }

        @Override
        public void visit(Image image) {
            super.visit(image);
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            super.visit(indentedCodeBlock);
        }

        @Override
        public void visit(Link link) {
            super.visit(link);
        }

        @Override
        public void visit(ListItem listItem) {
            super.visit(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            super.visit(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            super.visit(paragraph);

        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            super.visit(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            super.visit(strongEmphasis);
        }

        @Override
        public void visit(Text text) {
            super.visit(text);
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            super.visit(linkReferenceDefinition);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            super.visit(customBlock);
        }

        @Override
        public void visit(CustomNode customNode) {
            super.visit(customNode);
        }

    }


}
