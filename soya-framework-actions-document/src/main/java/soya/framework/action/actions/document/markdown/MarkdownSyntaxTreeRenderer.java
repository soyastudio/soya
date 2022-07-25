package soya.framework.action.actions.document.markdown;

import soya.framework.action.Command;
import soya.framework.commons.knowledge.renderers.JsonTreeStructureRenderer;

@Command(group = "markdown", name = "syntax-tree", httpMethod = Command.HttpMethod.POST)
public class MarkdownSyntaxTreeRenderer extends MarkdownAction<String> {

    @Override
    protected String process() throws Exception {
        //return new TreePathRenderer().getValueMethod("getTitle").render(knowledgeTree);

        return new JsonTreeStructureRenderer().render(knowledgeTree);
    }
}
