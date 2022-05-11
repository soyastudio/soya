package soya.framework.tasks.markdown;

import org.commonmark.node.Node;

import java.util.EventObject;
import java.util.UUID;

public class MarkdownEvent extends EventObject {
    private final String id;

    public MarkdownEvent(Node node) {
        super(node);
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
