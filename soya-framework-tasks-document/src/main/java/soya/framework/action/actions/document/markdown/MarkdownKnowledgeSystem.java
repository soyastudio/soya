package soya.framework.action.actions.document.markdown;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import soya.framework.knowledge.KnowledgeBuildException;
import soya.framework.knowledge.KnowledgeTree;
import soya.framework.knowledge.KnowledgeTreeNode;
import soya.framework.knowledge.generic.GenericKnowledgeSystem;
import soya.framework.knowledge.generic.GenericKnowledgeTree;

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

        private KnowledgeTree<Document, MarkdownNode> tree;
        private Stack<KnowledgeTreeNode<MarkdownNode>> stack = new Stack<>();

        @Override
        public KnowledgeTree<Document, MarkdownNode> digester(Document knowledge) throws KnowledgeBuildException {
            this.tree = GenericKnowledgeTree.newInstance(knowledge, new MarkdownNode(knowledge));
            stack.push(tree.root());
            knowledge.accept(this);

            stack.clear();
            return tree;
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            stack.peek().origin().add(blockQuote);
            super.visit(blockQuote);
        }

        @Override
        public void visit(BulletList bulletList) {
            stack.peek().origin().add(bulletList);
            super.visit(bulletList);
        }

        @Override
        public void visit(Code code) {
            stack.peek().origin().add(code);
            super.visit(code);
        }

        @Override
        public void visit(Document document) {
            super.visit(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            stack.peek().origin().add(emphasis);
            super.visit(emphasis);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            stack.peek().origin().add(fencedCodeBlock);
            super.visit(fencedCodeBlock);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            stack.peek().origin().add(hardLineBreak);
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            MarkdownNode node = new MarkdownNode(heading);

            KnowledgeTreeNode<MarkdownNode> last = stack.peek();
            while (last.origin().getLevel() >= node.getLevel()) {
                stack.pop();
                last = stack.peek();
            }

            stack.push(tree.create(last, node));
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            stack.peek().origin().add(thematicBreak);
            super.visit(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            stack.peek().origin().add(htmlInline);
            super.visit(htmlInline);
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            stack.peek().origin().add(htmlBlock);
            super.visit(htmlBlock);
        }

        @Override
        public void visit(Image image) {
            stack.peek().origin().add(image);
            super.visit(image);
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            stack.peek().origin().add(indentedCodeBlock);
            super.visit(indentedCodeBlock);
        }

        @Override
        public void visit(Link link) {
            stack.peek().origin().add(link);
            super.visit(link);
        }

        @Override
        public void visit(ListItem listItem) {
            stack.peek().origin().add(listItem);
            super.visit(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            stack.peek().origin().add(orderedList);
            super.visit(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            stack.peek().origin().add(paragraph);
            super.visit(paragraph);

        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            stack.peek().origin().add(softLineBreak);
            super.visit(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            stack.peek().origin().add(strongEmphasis);
            super.visit(strongEmphasis);
        }

        @Override
        public void visit(Text text) {
            MarkdownNode node = stack.peek().origin();
            stack.peek().origin().add(text);

            super.visit(text);
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            stack.peek().origin().add(linkReferenceDefinition);
            super.visit(linkReferenceDefinition);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            stack.peek().origin().add(customBlock);
            super.visit(customBlock);
        }

        @Override
        public void visit(CustomNode customNode) {
            stack.peek().origin().add(customNode);
            super.visit(customNode);
        }

    }

}
