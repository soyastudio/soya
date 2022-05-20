package soya.framework.tasks.markdown;

import soya.framework.core.Command;
import soya.framework.kt.renderers.JsonTreeStructureRenderer;

@Command(group = "markdown", name = "syntax-tree", httpMethod = Command.HttpMethod.POST)
public class MarkdownSyntaxTreeRenderer extends MarkdownTask<String> {

    @Override
    protected String process() throws Exception {
        //return new TreePathRenderer().getValueMethod("getTitle").render(knowledgeTree);

        return new JsonTreeStructureRenderer().render(knowledgeTree);
    }
}
