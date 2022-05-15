package soya.framework.tasks.markdown;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import soya.framework.core.CommandGroup;
import soya.framework.core.CommandOption;
import soya.framework.core.Task;
import soya.framework.kt.KnowledgeTree;

@CommandGroup(group = "markdown",
        title = "Markdown Tool",
        description = "Markdown parsing and conversion toolkit.")
public abstract class MarkdownTask<T> extends Task<T> {

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
