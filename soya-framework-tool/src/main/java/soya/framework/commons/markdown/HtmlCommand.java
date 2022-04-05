package soya.framework.commons.markdown;

import org.commonmark.node.Visitor;

public class HtmlCommand extends MarkdownCommand{
    @Override
    protected Visitor visitor() {
        return null;
    }

    @Override
    protected String process(Visitor visitor) throws Exception {
        return null;
    }
}
