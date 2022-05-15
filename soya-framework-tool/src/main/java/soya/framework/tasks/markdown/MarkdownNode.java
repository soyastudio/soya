package soya.framework.tasks.markdown;

import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.ArrayList;
import java.util.List;

public class MarkdownNode {
    private final int level;

    private Title title = new Title();
    private List<Node> children = new ArrayList<>();

    public MarkdownNode(Document document) {
        this.level = 0;
    }

    public MarkdownNode(Heading heading) {
        this.level = heading.getLevel();
    }

    public int getLevel() {
        return level;
    }

    public Title getTitle() {
        return title;
    }

    public void add(Node node) {

        if (isHeadingPart(node)) {
            title.add(node);
        } else {
            children.add(node);
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    private boolean isHeadingPart(Node node) {
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof Heading) {
                return true;
            } else {
                parent = parent.getParent();
            }

        }

        return false;
    }

    public static class Title {
        private Text text;
        private List<Node> nodes = new ArrayList<>();

        private void add(Node node) {
            nodes.add(node);
            if (node instanceof Text) {
                this.text = (Text) node;
            }
        }

        public String toString() {
            return text == null ? "" : text.getLiteral();
        }
    }
}
