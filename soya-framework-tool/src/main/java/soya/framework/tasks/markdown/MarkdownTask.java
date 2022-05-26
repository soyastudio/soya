package soya.framework.tasks.markdown;

import org.commonmark.node.*;
import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Task;
import soya.framework.knowledge.KnowledgeTree;

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
