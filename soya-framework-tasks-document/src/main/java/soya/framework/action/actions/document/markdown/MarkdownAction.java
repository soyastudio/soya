package soya.framework.action.actions.document.markdown;

import org.commonmark.node.*;
import soya.framework.action.CommandGroup;
import soya.framework.action.CommandOption;
import soya.framework.action.Action;
import soya.framework.knowledge.KnowledgeTree;

@CommandGroup(group = "markdown",
        title = "Markdown Tool",
        description = "Markdown parsing and conversion commands.")
public abstract class MarkdownAction<T> extends Action<T> {

    @CommandOption(option = "m", required = true, dataForProcessing = true)
    protected String markdown;

    protected KnowledgeTree<Document, MarkdownNode> knowledgeTree;

    @Override
    protected void init() throws Exception {
        knowledgeTree = MarkdownKnowledgeSystem.knowledgeTree(markdown);
    }

    @Override
    public T execute() throws Exception {
        return process();
    }

    protected abstract T process() throws Exception;

}
