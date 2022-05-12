package soya.framework.tasks.markdown;

import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import java.util.ArrayList;
import java.util.List;

public class MarkdownNode {
    private final int level;
    private String title;
    private List<Node> nodeList = new ArrayList<>();

    public MarkdownNode(Document document) {
        this.level = 0;
    }

    public MarkdownNode(Heading heading) {
        this.level = heading.getLevel();
    }

    public int getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
