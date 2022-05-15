package soya.framework.tasks.markdown;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.kt.generic.TreePathRenderer;

import javax.swing.tree.*;

@Command(group = "markdown", name = "syntax-tree", httpMethod = Command.HttpMethod.POST)
public class MarkdownSyntaxTreeRenderer extends MarkdownTask<String> {

    @Override
    protected String process() throws Exception {
        return new TreePathRenderer().getValueMethod("getTitle").render(knowledgeTree);
    }
}
